const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_EMAIL = "kabirtechlab26@gmail.com";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("community_posts").where("approved", "==", true).get());
  await assertFails(unauthDb.collection("video_requests").where("visibility", "==", "PUBLIC").get());
  await assertFails(unauthDb.collection("announcements").where("visibility", "==", "PUBLIC").get());
});

test("User profile create, partial update, cross-user isolation, and shadow-field rejection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 2000);

  // Valid create matching Kotlin UserProfile
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Kabir Fan",
      language: "bn",
      themeMode: "dark",
      xp: 120,
      streakDays: 3,
      createdAt: now,
      updatedAt: now,
    })
  );

  // Valid partial update
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      xp: 170,
      language: "en",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject ghost/shadow field
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject future timestamp
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      displayName: "Bob",
      language: "en",
      themeMode: "dark",
      xp: 0,
      streakDays: 0,
      createdAt: new Date(Date.now() + 60000),
      updatedAt: now,
    })
  );

  // Cross-user read/write blocked
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).update({ xp: 999, updatedAt: now }));
});

test("User PII private info and subcollections (notes, playlists, progress) enforce owner isolation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 2000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("private").doc("join_info").set({
      userId: ALICE_UID,
      fullName: "Tariq Ahmed",
      contact: "tariq@example.com",
      deviceModel: "Pixel 8",
      interests: "AI Tools,Stories",
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("private").doc("join_info").get());

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("notes").doc("note_1").set({
      userId: ALICE_UID,
      videoId: "ktl_vid_1",
      videoTitle: "Gemini AI Guide",
      timestampSeconds: 145,
      content: "Important prompt structure at 2:25",
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("notes").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("notes").get());

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("playlists").doc("pl_1").set({
      userId: ALICE_UID,
      name: "My AI Picks",
      videoIds: ["ktl_vid_1", "ktl_vid_2"],
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("progress").doc("ktl_vid_1").set({
      userId: ALICE_UID,
      itemId: "ktl_vid_1",
      itemType: "VIDEO",
      watchedSeconds: 320,
      totalSeconds: 640,
      saved: true,
      liked: true,
      completed: false,
      createdAt: now,
      updatedAt: now,
    })
  );
});

test("Community posts & video requests enforce filtered queries and safe updates", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const now = new Date(Date.now() - 2000);

  await assertSucceeds(
    aliceDb.collection("community_posts").doc("post_1").set({
      authorId: ALICE_UID,
      authorName: "Alice",
      postType: "QA",
      title: "How to optimize Bangla prompts?",
      body: "Sharing tips for better Bangla output.",
      likesCount: 0,
      repliesCount: 0,
      approved: true,
      reported: false,
      createdAt: now,
      updatedAt: now,
    })
  );

  // Filtered query succeeds, unfiltered query fails
  await assertSucceeds(aliceDb.collection("community_posts").where("approved", "==", true).get());
  await assertFails(aliceDb.collection("community_posts").get());

  // Bob can increment likes by 1, cannot tamper body or increment by 100
  await assertSucceeds(
    bobDb.collection("community_posts").doc("post_1").update({
      likesCount: 1,
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(
    bobDb.collection("community_posts").doc("post_1").update({
      likesCount: 100,
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(
    bobDb.collection("community_posts").doc("post_1").update({
      body: "Tampered",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Announcements require verified admin email; spoofed email_verified=false is rejected", async () => {
  const realAdminDb = testEnv
    .authenticatedContext("admin_1", { email: ADMIN_EMAIL, email_verified: true })
    .firestore();
  const spoofAdminDb = testEnv
    .authenticatedContext("spoof_1", { email: ADMIN_EMAIL, email_verified: false })
    .firestore();
  const regularDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 2000);

  await assertFails(
    spoofAdminDb.collection("announcements").doc("ann_1").set({
      authorId: "spoof_1",
      titleBn: "নকল ঘোষণা",
      titleEn: "Spoof",
      bodyBn: "টেস্ট",
      bodyEn: "Test",
      pinned: true,
      visibility: "PUBLIC",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    realAdminDb.collection("announcements").doc("ann_1").set({
      authorId: "admin_1",
      titleBn: "নতুন এআই সিরিজ আসছে!",
      titleEn: "New AI Series Coming!",
      bodyBn: "শুক্রবার রাত ৮টায় লাইভ।",
      bodyEn: "Live Friday at 8 PM.",
      pinned: true,
      visibility: "PUBLIC",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(regularDb.collection("announcements").where("visibility", "==", "PUBLIC").get());
  await assertFails(regularDb.collection("announcements").get());
});
