// Usage examples (v0 Scripts runner will handle execution):
//   node scripts/generate-seed.ts --users=50000 --follows=500000 --posts=200000 --likes=2000000 --comments=500000 --seed=42 --outDir=public/data/seed-large
// Defaults produce a moderate dataset if flags are omitted.

import {
  asAsyncIterable,
  generateComments,
  generateFollows,
  generateLikes,
  generatePosts,
  generateUsers,
  getRandom,
  writeJsonArrayStream,
} from "../lib/seed/generator.js"
import { mkdirSync } from "node:fs"
import { join } from "node:path"

type Args = {
  users: number
  follows: number
  posts: number
  likes: number
  comments: number
  seed?: number
  outDir: string
}

function parseArgs(): Args {
  const args = process.argv.slice(2)
  const get = (k: string) => {
    const match = args.find((a) => a.startsWith(`--${k}=`))
    return match ? match.split("=")[1] : undefined
  }
  const num = (k: string, d: number) => {
    const v = get(k)
    return v ? Number.parseInt(v, 10) : d
  }
  const outDir = get("outDir") ?? "public/data/seed-default"
  const seed = get("seed") ? Number.parseInt(get("seed")!, 10) : undefined
  return {
    users: num("users", 10000),
    follows: num("follows", 100000),
    posts: num("posts", 50000),
    likes: num("likes", 200000),
    comments: num("comments", 100000),
    seed,
    outDir,
  }
}

async function main() {
  const cfg = parseArgs()
  console.log("[v0] Seed config:", cfg)
  mkdirSync(cfg.outDir, { recursive: true })
  const rand = getRandom(cfg.seed)

  console.time("[v0] generate:users")
  const users = generateUsers(rand, cfg.users)
  console.timeEnd("[v0] generate:users")

  console.time("[v0] generate:follows")
  const follows = generateFollows(rand, cfg.users, cfg.follows)
  console.timeEnd("[v0] generate:follows")

  console.time("[v0] generate:posts")
  const posts = generatePosts(rand, cfg.users, cfg.posts)
  console.timeEnd("[v0] generate:posts")

  console.time("[v0] generate:likes")
  const likes = generateLikes(rand, cfg.users, cfg.posts, cfg.likes)
  console.timeEnd("[v0] generate:likes")

  console.time("[v0] generate:comments")
  const comments = generateComments(rand, cfg.users, cfg.posts, cfg.comments)
  console.timeEnd("[v0] generate:comments")

  const out = (f: string) => join(cfg.outDir, f)

  console.time("[v0] write:users")
  await writeJsonArrayStream(out("users.json"), asAsyncIterable(users))
  console.timeEnd("[v0] write:users")

  console.time("[v0] write:follows")
  await writeJsonArrayStream(out("follows.json"), asAsyncIterable(follows))
  console.timeEnd("[v0] write:follows")

  console.time("[v0] write:posts")
  await writeJsonArrayStream(out("posts.json"), asAsyncIterable(posts))
  console.timeEnd("[v0] write:posts")

  console.time("[v0] write:likes")
  await writeJsonArrayStream(out("likes.json"), asAsyncIterable(likes))
  console.timeEnd("[v0] write:likes")

  console.time("[v0] write:comments")
  await writeJsonArrayStream(out("comments.json"), asAsyncIterable(comments))
  console.timeEnd("[v0] write:comments")

  console.log("[v0] Done. Files written to:", cfg.outDir)
}

main().catch((err) => {
  console.error("[v0] Error generating seed data:", err)
  process.exit(1)
})
