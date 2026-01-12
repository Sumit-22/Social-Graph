// NOTE: Shared between scripts and potential API routes

export type User = {
  id: number
  username: string
  name: string
  joinedAt: string // ISO date
}

export type Follow = {
  followerId: number
  followeeId: number
  createdAt: string
}

export type Post = {
  id: number
  authorId: number
  content: string
  createdAt: string
}

export type Like = {
  id: number
  userId: number
  postId: number
  createdAt: string
}

export type Comment = {
  id: number
  userId: number
  postId: number
  content: string
  createdAt: string
}

export type SeedConfig = {
  users: number
  follows: number
  posts: number
  likes: number
  comments: number
  seed?: number
}

function mulberry32(seed: number) {
  let t = seed >>> 0
  return () => {
    t += 0x6d2b79f5
    let r = Math.imul(t ^ (t >>> 15), 1 | t)
    r ^= r + Math.imul(r ^ (r >>> 7), 61 | r)
    return ((r ^ (r >>> 14)) >>> 0) / 4294967296
  }
}

// Skewed random (Zipf-like) to simulate power-law popularity
function skewedIndex(rand: () => number, n: number, skew = 1.07) {
  // clamp skew > 1 for stronger head
  const u = rand()
  const x = Math.pow(u, -1 / (skew - 1)) // Pareto-like
  const idx = Math.floor(Math.min(n - 1, Math.max(0, x % n)))
  return idx
}

function randomInt(rand: () => number, min: number, max: number) {
  return Math.floor(rand() * (max - min + 1)) + min
}

function randomChoice<T>(rand: () => number, arr: T[]) {
  return arr[Math.floor(rand() * arr.length)]
}

function randomDateISO(rand: () => number, daysBack = 365) {
  const now = Date.now()
  const past = now - daysBack * 24 * 60 * 60 * 1000
  const t = randomInt(rand, past, now)
  return new Date(t).toISOString()
}

function sentence(rand: () => number, minWords = 3, maxWords = 20) {
  const words = [
    "social",
    "feed",
    "ranking",
    "realtime",
    "graph",
    "network",
    "post",
    "comment",
    "like",
    "follow",
    "engagement",
    "signal",
    "score",
    "decay",
    "affinity",
    "AI",
    "system",
    "event",
    "stream",
    "cache",
    "redis",
    "kafka",
    "neo4j",
    "postgres",
    "scale",
    "optimize",
    "trend",
    "topic",
    "interest",
    "ranker",
    "feature",
    "boost",
  ]
  const n = randomInt(rand, minWords, maxWords)
  const out: string[] = []
  for (let i = 0; i < n; i++) out.push(randomChoice(rand, words))
  const s = out.join(" ")
  return s.charAt(0).toUpperCase() + s.slice(1) + "."
}

export function generateUsers(rand: () => number, count: number): User[] {
  const users: User[] = []
  for (let i = 1; i <= count; i++) {
    users.push({
      id: i,
      username: `user${i}`,
      name: `User ${i}`,
      joinedAt: randomDateISO(rand, 900),
    })
  }
  return users
}

export function generateFollows(rand: () => number, usersCount: number, edges: number): Follow[] {
  const follows: Follow[] = []
  const seen = new Set<string>()
  for (let i = 0; i < edges; i++) {
    const follower = randomInt(rand, 1, usersCount)
    let followee: number
    // Preferential attachment: popular users get more followers
    if (rand() < 0.7) {
      followee = skewedIndex(rand, usersCount, 1.25) + 1
    } else {
      followee = randomInt(rand, 1, usersCount)
    }
    if (follower === followee) {
      i--
      continue
    }
    const key = `${follower}->${followee}`
    if (seen.has(key)) {
      i--
      continue
    }
    seen.add(key)
    follows.push({
      followerId: follower,
      followeeId: followee,
      createdAt: randomDateISO(rand, 800),
    })
  }
  return follows
}

export function generatePosts(rand: () => number, usersCount: number, count: number): Post[] {
  const posts: Post[] = []
  for (let i = 1; i <= count; i++) {
    let authorId: number
    if (rand() < 0.7) {
      authorId = skewedIndex(rand, usersCount, 1.15) + 1
    } else {
      authorId = randomInt(rand, 1, usersCount)
    }
    posts.push({
      id: i,
      authorId,
      content: sentence(rand, 6, 24),
      createdAt: randomDateISO(rand, 120),
    })
  }
  return posts
}

export function generateLikes(rand: () => number, usersCount: number, postsCount: number, count: number): Like[] {
  const likes: Like[] = []
  for (let i = 1; i <= count; i++) {
    const userId = randomInt(rand, 1, usersCount)
    const postId = rand() < 0.8 ? skewedIndex(rand, postsCount, 1.2) + 1 : randomInt(rand, 1, postsCount)
    likes.push({
      id: i,
      userId,
      postId,
      createdAt: randomDateISO(rand, 60),
    })
  }
  return likes
}

export function generateComments(rand: () => number, usersCount: number, postsCount: number, count: number): Comment[] {
  const comments: Comment[] = []
  for (let i = 1; i <= count; i++) {
    const userId = randomInt(rand, 1, usersCount)
    const postId = rand() < 0.75 ? skewedIndex(rand, postsCount, 1.25) + 1 : randomInt(rand, 1, postsCount)
    comments.push({
      id: i,
      userId,
      postId,
      content: sentence(rand, 4, 18),
      createdAt: randomDateISO(rand, 45),
    })
  }
  return comments
}

export function getRandom(seed: number | undefined) {
  return mulberry32(seed ?? 42)
}

// Streaming writers to handle very large arrays without huge memory usage
import { createWriteStream } from "node:fs"
import { mkdirSync } from "node:fs"
import { dirname } from "node:path"

export async function writeJsonArrayStream<T>(filePath: string, iter: Iterable<T> | AsyncIterable<T>) {
  mkdirSync(dirname(filePath), { recursive: true })
  const stream = createWriteStream(filePath, { encoding: "utf-8" })
  await new Promise<void>((resolve, reject) => {
    stream.once("open", () => {
      stream.write("[")
      let first = true
      const writeNext = async () => {
        try {
          for await (const item of iter as any) {
            const chunk = (first ? "" : ",") + JSON.stringify(item)
            if (!stream.write(chunk)) {
              await new Promise((res) => stream.once("drain", res))
            }
            first = false
          }
          stream.end("]")
        } catch (e) {
          reject(e)
        }
      }
      writeNext()
    })
    stream.on("error", reject)
    stream.on("finish", () => resolve())
  })
}

// Convert arrays to async generator for streaming writer
export async function* asAsyncIterable<T>(arr: T[]) {
  for (const item of arr) {
    yield item
  }
}
