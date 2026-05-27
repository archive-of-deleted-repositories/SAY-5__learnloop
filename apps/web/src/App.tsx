import { useState } from "react";
import { Practice } from "./components/Practice";
import { StudentDashboard } from "./components/StudentDashboard";
import { TeacherDashboard } from "./components/TeacherDashboard";

type Tab = "practice" | "student" | "teacher";

const DEMO_LEARNER = 1;
const DEMO_CLASS = "period-1";

export function App() {
  const [tab, setTab] = useState<Tab>("practice");

  return (
    <main>
      <header className="app">
        <h1>LearnLoop</h1>
        <nav className="tabs" role="tablist">
          <button
            role="tab"
            aria-selected={tab === "practice"}
            onClick={() => setTab("practice")}
          >
            Practice
          </button>
          <button
            role="tab"
            aria-selected={tab === "student"}
            onClick={() => setTab("student")}
          >
            My progress
          </button>
          <button
            role="tab"
            aria-selected={tab === "teacher"}
            onClick={() => setTab("teacher")}
          >
            Class view
          </button>
        </nav>
      </header>

      {tab === "practice" && <Practice learnerId={DEMO_LEARNER} />}
      {tab === "student" && <StudentDashboard learnerId={DEMO_LEARNER} />}
      {tab === "teacher" && <TeacherDashboard classGroup={DEMO_CLASS} />}
    </main>
  );
}
