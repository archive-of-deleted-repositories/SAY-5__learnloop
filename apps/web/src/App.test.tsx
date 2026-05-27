import { render, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { App } from "./App";

function renderApp() {
  const client = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(
    <QueryClientProvider client={client}>
      <App />
    </QueryClientProvider>,
  );
}

beforeEach(() => {
  vi.stubGlobal(
    "fetch",
    vi.fn(async () =>
      new Response(JSON.stringify({ learnerRating: 1200, nextQuestion: null }), {
        status: 200,
        headers: { "content-type": "application/json" },
      }),
    ),
  );
});

test("renders the app title", () => {
  renderApp();
  expect(screen.getByRole("heading", { name: /learnloop/i })).toBeInTheDocument();
});

test("shows the three navigation tabs", () => {
  renderApp();
  expect(screen.getByRole("tab", { name: /practice/i })).toBeInTheDocument();
  expect(screen.getByRole("tab", { name: /my progress/i })).toBeInTheDocument();
  expect(screen.getByRole("tab", { name: /class view/i })).toBeInTheDocument();
});
