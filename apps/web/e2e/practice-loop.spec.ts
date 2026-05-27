import { test, expect } from "@playwright/test";

// A learner answers a streak correctly: their rating rises and mastery advances.
// The teacher class view then reflects the class state.
test("a correct streak raises rating and advances mastery", async ({ page }) => {
  await page.goto("/");

  await page.getByRole("tab", { name: /practice/i }).click();
  // Default skill (Fractions) is selected; wait for a question.
  await expect(page.getByTestId("prompt")).toBeVisible();

  // First answer to reveal the rating panel.
  await page.getByTestId("answer-correct").click();
  await expect(page.getByTestId("rating")).toBeVisible();
  const firstRating = Number(await page.getByTestId("rating").innerText());

  // Answer a streak correctly.
  for (let i = 0; i < 18; i++) {
    await page.getByTestId("answer-correct").click();
    // wait for the next question prompt to settle between clicks
    await expect(page.getByTestId("answer-correct")).toBeEnabled();
  }

  const lastRating = Number(await page.getByTestId("rating").innerText());
  expect(lastRating).toBeGreaterThan(firstRating);

  // Mastery should have climbed above Novice.
  await expect(page.getByTestId("result")).not.toContainText("Novice");

  // Student progress reflects the higher rating.
  await page.getByRole("tab", { name: /my progress/i }).click();
  await expect(page.getByTestId("student-skills")).toBeVisible();
  const skillRating = Number(await page.getByTestId("skill-rating-1").innerText());
  expect(skillRating).toBeGreaterThan(1200);

  // Teacher class view renders the distribution and a weakest skill.
  await page.getByRole("tab", { name: /class view/i }).click();
  await expect(page.getByTestId("distributions")).toBeVisible();
  await expect(page.getByTestId("weakest-skill")).toContainText(/median score/i);
  await expect(page.getByTestId("roster")).toBeVisible();
});
