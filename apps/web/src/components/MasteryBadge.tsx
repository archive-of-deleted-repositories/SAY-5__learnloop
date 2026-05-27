import type { MasteryLevel } from "../api/client";

const LABELS: Record<MasteryLevel, string> = {
  NOVICE: "Novice",
  DEVELOPING: "Developing",
  PROFICIENT: "Proficient",
  MASTERED: "Mastered",
};

export function MasteryBadge({ level }: { level: MasteryLevel }) {
  return <span className={`badge ${level}`}>{LABELS[level]}</span>;
}
