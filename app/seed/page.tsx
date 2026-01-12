import Link from "next/link"

export default function SeedDocsPage() {
  return (
    <main className="mx-auto max-w-3xl px-6 py-10">
      <h1 className="text-2xl font-semibold text-pretty">Seed Data Generator</h1>
      <p className="mt-4 leading-relaxed">
        High-volume JSON datasets (users, follows, posts, likes, comments) can be generated with the script in
        <code className="px-1"> /scripts/generate-seed.ts</code>. By default, it writes to
        <code className="px-1"> /public/data/seed-default</code>, and you can customize sizes with CLI flags.
      </p>

      <section className="mt-6">
        <h2 className="text-xl font-medium">Quick Start</h2>
        <ol className="list-decimal ml-5 mt-2 leading-relaxed">
          <li>Run the script with your desired sizes (e.g., 50k users, 500k follows, 200k posts).</li>
          <li>Find the generated JSON files in the chosen output folder, ready to import into your services.</li>
        </ol>
      </section>

      <section className="mt-6">
        <h2 className="text-xl font-medium">Schema Samples</h2>
        <ul className="list-disc ml-5 mt-2">
          <li>
            <Link href="/data/users.sample.json" className="underline">
              users.sample.json
            </Link>
          </li>
          <li>
            <Link href="/data/follows.sample.json" className="underline">
              follows.sample.json
            </Link>
          </li>
          <li>
            <Link href="/data/posts.sample.json" className="underline">
              posts.sample.json
            </Link>
          </li>
          <li>
            <Link href="/data/likes.sample.json" className="underline">
              likes.sample.json
            </Link>
          </li>
          <li>
            <Link href="/data/comments.sample.json" className="underline">
              comments.sample.json
            </Link>
          </li>
        </ul>
      </section>

      <section className="mt-6">
        <h2 className="text-xl font-medium">Scaling tips</h2>
        <ul className="list-disc ml-5 mt-2 leading-relaxed">
          <li>
            Prefer a dedicated output directory like <code className="px-1">/public/data/seed-large</code>.
          </li>
          <li>
            Use the <code className="px-1">--seed</code> flag for reproducible datasets.
          </li>
          <li>The generator uses skewed distributions to simulate real social graphs and popularity.</li>
        </ul>
      </section>
    </main>
  )
}
