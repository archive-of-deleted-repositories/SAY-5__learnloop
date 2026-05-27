import { render, screen } from "@testing-library/react";
import { MasteryBadge } from "./MasteryBadge";

test("renders the human label for a mastery level", () => {
  render(<MasteryBadge level="MASTERED" />);
  const badge = screen.getByText("Mastered");
  expect(badge).toHaveClass("badge", "MASTERED");
});
