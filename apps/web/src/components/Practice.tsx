import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  fetchNext,
  submitAnswer,
  type SubmitAnswerResponse,
} from "../api/client";
import { MasteryBadge } from "./MasteryBadge";

const SKILLS = [
  { id: 1, name: "Fractions" },
  { id: 2, name: "Algebra" },
  { id: 3, name: "Geometry" },
];

export function Practice({ learnerId }: { learnerId: number }) {
  const [skillId, setSkillId] = useState(1);
  const [last, setLast] = useState<SubmitAnswerResponse | null>(null);
  const queryClient = useQueryClient();

  const next = useQuery({
    queryKey: ["next", learnerId, skillId],
    queryFn: () => fetchNext(learnerId, skillId),
  });

  const answer = useMutation({
    mutationFn: (correct: boolean) =>
      submitAnswer({
        learnerId,
        skillId,
        questionId: next.data!.nextQuestion!.questionId,
        correct,
      }),
    onSuccess: (res) => {
      setLast(res);
      queryClient.invalidateQueries({ queryKey: ["next", learnerId, skillId] });
      queryClient.invalidateQueries({ queryKey: ["student", learnerId] });
    },
  });

  const question = next.data?.nextQuestion;

  return (
    <section>
      <div className="skill-pick" role="group" aria-label="Choose a skill">
        {SKILLS.map((s) => (
          <button
            key={s.id}
            aria-pressed={s.id === skillId}
            onClick={() => {
              setSkillId(s.id);
              setLast(null);
            }}
          >
            {s.name}
          </button>
        ))}
      </div>

      <div className="card">
        {next.isLoading && <p className="muted">Loading question...</p>}
        {question ? (
          <>
            <p className="prompt" data-testid="prompt">
              {question.prompt}
            </p>
            <p className="muted" data-testid="difficulty">
              Difficulty {question.difficulty.toFixed(0)}
            </p>
            <div className="answer-row">
              <button
                className="primary"
                data-testid="answer-correct"
                disabled={answer.isPending}
                onClick={() => answer.mutate(true)}
              >
                I got it right
              </button>
              <button
                className="ghost"
                data-testid="answer-wrong"
                disabled={answer.isPending}
                onClick={() => answer.mutate(false)}
              >
                I got it wrong
              </button>
            </div>
          </>
        ) : (
          !next.isLoading && <p className="muted">No question available.</p>
        )}
      </div>

      {last && (
        <div className="card" data-testid="result">
          <div className="stat-grid">
            <div className="stat">
              <div className="label">Your rating</div>
              <div className="value" data-testid="rating">
                {last.learnerRating.toFixed(0)}
              </div>
            </div>
            <div className="stat">
              <div className="label">Last expected</div>
              <div className="value">{(last.expectedScore * 100).toFixed(0)}%</div>
            </div>
            <div className="stat">
              <div className="label">Mastery</div>
              <div className="value">
                <MasteryBadge level={last.masteryLevel} />
              </div>
            </div>
          </div>
        </div>
      )}
    </section>
  );
}
