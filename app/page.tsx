import Link from "next/link"

export default function Home() {
  return (
    <main className="min-h-dvh flex flex-col items-center justify-center gap-6 px-6 py-10">
      <h1 className="text-3xl md:text-4xl font-semibold text-pretty text-foreground">SocialGraphAI Backend Scaffold</h1>
      <p className="text-muted-foreground max-w-prose text-center leading-relaxed">
        A complete Spring Boot microservices scaffold for a real-time social feed ranking engine with Kafka, Redis,
        Postgres, and Neo4j. Download the project and run the backend with Docker Compose, then call the APIs to see
        rankings flow from post creation to personalized feeds.
      </p>
      <div className="flex items-center gap-3">
        <Link
          className="inline-flex items-center rounded-md bg-primary px-4 py-2 text-primary-foreground"
          href="https://github.com/"
        >
          {"How to run (see below)"}
        </Link>
        <a className="inline-flex items-center rounded-md border border-border px-4 py-2" href="#run-instructions">
          {"Quickstart"}
        </a>
      </div>

      <section id="run-instructions" className="max-w-2xl w-full mt-10">
        <h2 className="text-xl font-medium mb-2">Run instructions</h2>
        <ol className="list-decimal pl-5 space-y-2 text-sm leading-6 text-muted-foreground">
          <li>
            Open <code>backend/socialgraphai/docker-compose.yml</code> and run your Docker stack.
          </li>
          <li>
            Start each service with your IDE or <code>mvn spring-boot:run</code>:
            <ul className="list-disc pl-5">
              <li>user-service (8080)</li>
              <li>feed-ingestor (8090)</li>
              <li>feed-ranker (8100)</li>
              <li>feed-api (8081)</li>
            </ul>
          </li>
          <li>Create users and relationships, then create a post to see a ranked feed.</li>
        </ol>
        <div className="mt-4 text-sm text-muted-foreground">
          Endpoints:
          <ul className="list-disc pl-5">
            <li>POST http://localhost:8080/users/create {"{ username }"}</li>
            <li>
              POST http://localhost:8080/users/{"{from}"}/follow/{"{to}"}
            </li>
            <li>POST http://localhost:8090/post/create {"{ authorId, content }"}</li>
            <li>GET http://localhost:8081/feed?userId=bob&limit=10</li>
          </ul>
        </div>
      </section>
    </main>
  )
}
