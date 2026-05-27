export type MasteryLevel = "NOVICE" | "DEVELOPING" | "PROFICIENT" | "MASTERED";

export interface QuestionView {
  questionId: number;
  prompt: string;
  difficulty: number;
}

export interface SubmitAnswerResponse {
  learnerRating: number;
  questionDifficulty: number;
  expectedScore: number;
  masteryLevel: MasteryLevel;
  masteryScore: number;
  nextQuestion: QuestionView | null;
}

export interface NextQuestionResponse {
  learnerRating: number;
  nextQuestion: QuestionView | null;
}

export interface SkillMasteryView {
  skillId: number;
  skillCode: string;
  skillName: string;
  rating: number;
  level: MasteryLevel;
  score: number;
}

export interface TrendPoint {
  questionId: number;
  correct: boolean;
  learnerRatingAfter: number;
}

export interface StudentDashboard {
  learnerId: number;
  name: string;
  skills: SkillMasteryView[];
  recentTrend: TrendPoint[];
}

export interface RosterEntry {
  learnerId: number;
  name: string;
  skills: SkillMasteryView[];
}

export interface SkillDistribution {
  skillId: number;
  skillCode: string;
  skillName: string;
  novice: number;
  developing: number;
  proficient: number;
  mastered: number;
  medianScore: number;
}

export interface TeacherDashboard {
  classGroup: string;
  roster: RosterEntry[];
  distributions: SkillDistribution[];
  weakestSkills: SkillDistribution[];
}

export interface QuestionDifficultyView {
  questionId: number;
  prompt: string;
  attempts: number;
  correct: number;
  successRate: number;
}

export interface WeakSkillReport {
  skillId: number;
  skillCode: string;
  skillName: string;
  medianMastery: number;
  failureRate: number;
  struggleScore: number;
  hardestQuestions: QuestionDifficultyView[];
}

export interface CohortAnalytics {
  classGroup: string;
  rankedSkills: WeakSkillReport[];
}

const BASE = "/api";

async function json<T>(res: Response): Promise<T> {
  if (!res.ok) {
    throw new Error(`request failed: ${res.status}`);
  }
  return (await res.json()) as T;
}

export async function fetchNext(
  learnerId: number,
  skillId: number,
): Promise<NextQuestionResponse> {
  return json(await fetch(`${BASE}/practice/next/${learnerId}/${skillId}`));
}

export async function submitAnswer(input: {
  learnerId: number;
  skillId: number;
  questionId: number;
  correct: boolean;
}): Promise<SubmitAnswerResponse> {
  const res = await fetch(`${BASE}/practice/answers`, {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify(input),
  });
  return json(res);
}

export async function fetchStudentDashboard(
  learnerId: number,
): Promise<StudentDashboard> {
  return json(await fetch(`${BASE}/dashboards/student/${learnerId}`));
}

export async function fetchTeacherDashboard(
  classGroup: string,
): Promise<TeacherDashboard> {
  return json(await fetch(`${BASE}/dashboards/teacher/${classGroup}`));
}

export async function fetchCohortAnalytics(
  classGroup: string,
): Promise<CohortAnalytics> {
  return json(await fetch(`${BASE}/dashboards/cohort/${classGroup}`));
}
