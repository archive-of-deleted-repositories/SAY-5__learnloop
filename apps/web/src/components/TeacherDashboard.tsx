import { useQuery } from "@tanstack/react-query";
import { fetchTeacherDashboard } from "../api/client";
import { CohortAnalytics } from "./CohortAnalytics";

export function TeacherDashboard({ classGroup }: { classGroup: string }) {
  const { data, isLoading } = useQuery({
    queryKey: ["teacher", classGroup],
    queryFn: () => fetchTeacherDashboard(classGroup),
  });

  if (isLoading || !data) {
    return <p className="muted">Loading class view...</p>;
  }

  const weakest = data.weakestSkills[0];

  return (
    <section>
      <div className="card">
        <h2>Class {data.classGroup}</h2>
        {weakest && (
          <p data-testid="weakest-skill">
            Weakest skill:{" "}
            <strong>{weakest.skillName}</strong>{" "}
            <span className="muted">(median score {(weakest.medianScore * 100).toFixed(0)}%)</span>
          </p>
        )}
      </div>

      <div className="card">
        <h3>Mastery distribution per skill</h3>
        <table data-testid="distributions">
          <thead>
            <tr>
              <th>Skill</th>
              <th>Novice</th>
              <th>Developing</th>
              <th>Proficient</th>
              <th>Mastered</th>
              <th>Median</th>
            </tr>
          </thead>
          <tbody>
            {data.distributions.map((d) => (
              <tr key={d.skillId}>
                <td>{d.skillName}</td>
                <td>{d.novice}</td>
                <td>{d.developing}</td>
                <td>{d.proficient}</td>
                <td>{d.mastered}</td>
                <td>{(d.medianScore * 100).toFixed(0)}%</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <CohortAnalytics classGroup={data.classGroup} />

      <div className="card">
        <h3>Roster</h3>
        <table data-testid="roster">
          <thead>
            <tr>
              <th>Student</th>
              {data.distributions.map((d) => (
                <th key={d.skillId}>{d.skillName}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data.roster.map((r) => (
              <tr key={r.learnerId}>
                <td>{r.name}</td>
                {r.skills.map((s) => (
                  <td key={s.skillId}>{s.level.charAt(0) + s.level.slice(1).toLowerCase()}</td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
