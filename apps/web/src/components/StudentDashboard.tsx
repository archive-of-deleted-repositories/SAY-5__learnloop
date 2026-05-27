import { useQuery } from "@tanstack/react-query";
import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { fetchStudentDashboard } from "../api/client";
import { MasteryBadge } from "./MasteryBadge";

export function StudentDashboard({ learnerId }: { learnerId: number }) {
  const { data, isLoading } = useQuery({
    queryKey: ["student", learnerId],
    queryFn: () => fetchStudentDashboard(learnerId),
  });

  if (isLoading || !data) {
    return <p className="muted">Loading dashboard...</p>;
  }

  const trend = data.recentTrend.map((p, i) => ({
    n: i + 1,
    rating: Math.round(p.learnerRatingAfter),
  }));

  return (
    <section>
      <div className="card">
        <h2>{data.name}</h2>
        <table data-testid="student-skills">
          <thead>
            <tr>
              <th>Skill</th>
              <th>Rating</th>
              <th>Mastery</th>
            </tr>
          </thead>
          <tbody>
            {data.skills.map((s) => (
              <tr key={s.skillId} data-testid={`skill-row-${s.skillId}`}>
                <td>{s.skillName}</td>
                <td data-testid={`skill-rating-${s.skillId}`}>{s.rating.toFixed(0)}</td>
                <td>
                  <MasteryBadge level={s.level} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="card">
        <h3>Recent rating trend</h3>
        {trend.length > 1 ? (
          <ResponsiveContainer width="100%" height={220}>
            <LineChart data={trend}>
              <CartesianGrid strokeDasharray="3 3" stroke="#2d3a4f" />
              <XAxis dataKey="n" stroke="#8b98a9" />
              <YAxis domain={["dataMin - 20", "dataMax + 20"]} stroke="#8b98a9" />
              <Tooltip />
              <Line type="monotone" dataKey="rating" stroke="#4f9cf9" dot={false} />
            </LineChart>
          </ResponsiveContainer>
        ) : (
          <p className="muted">Answer a few questions to see your trend.</p>
        )}
      </div>
    </section>
  );
}
