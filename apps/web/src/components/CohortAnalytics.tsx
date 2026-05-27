import { useQuery } from "@tanstack/react-query";
import { fetchCohortAnalytics } from "../api/client";

export function CohortAnalytics({ classGroup }: { classGroup: string }) {
  const { data, isLoading } = useQuery({
    queryKey: ["cohort", classGroup],
    queryFn: () => fetchCohortAnalytics(classGroup),
  });

  if (isLoading || !data) {
    return <p className="muted">Loading cohort analytics...</p>;
  }

  const worst = data.rankedSkills[0];

  return (
    <div className="card" data-testid="cohort">
      <h3>Where the class struggles</h3>
      <table data-testid="cohort-ranking">
        <thead>
          <tr>
            <th>Skill</th>
            <th>Median mastery</th>
            <th>Failure rate</th>
            <th>Struggle</th>
          </tr>
        </thead>
        <tbody>
          {data.rankedSkills.map((s) => (
            <tr key={s.skillId}>
              <td>{s.skillName}</td>
              <td>{(s.medianMastery * 100).toFixed(0)}%</td>
              <td>{(s.failureRate * 100).toFixed(0)}%</td>
              <td>{(s.struggleScore * 100).toFixed(0)}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {worst && worst.hardestQuestions.length > 0 && (
        <div data-testid="drill-down">
          <h4>
            Hardest items in {worst.skillName}
          </h4>
          <table>
            <thead>
              <tr>
                <th>Question</th>
                <th>Attempts</th>
                <th>Class success</th>
              </tr>
            </thead>
            <tbody>
              {worst.hardestQuestions.map((q) => (
                <tr key={q.questionId}>
                  <td>{q.prompt}</td>
                  <td>{q.attempts}</td>
                  <td>{(q.successRate * 100).toFixed(0)}%</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
