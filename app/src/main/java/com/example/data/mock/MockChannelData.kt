package com.example.data.mock

import com.example.R
import com.example.data.model.Announcement
import com.example.data.model.CommunityEvent
import com.example.data.model.CommunityPost
import com.example.data.model.CourseLesson
import com.example.data.model.CuratedPlaylist
import com.example.data.model.FlashcardItem
import com.example.data.model.GlossaryTerm
import com.example.data.model.NotificationItem
import com.example.data.model.PollItem
import com.example.data.model.PollOption
import com.example.data.model.PromptTemplate
import com.example.data.model.QuizQuestion
import com.example.data.model.ResourceItem
import com.example.data.model.StoryCharacter
import com.example.data.model.StoryPart
import com.example.data.model.StorySeries
import com.example.data.model.ToolDirectoryItem
import com.example.data.model.VideoChapter
import com.example.data.model.VideoItem
import com.example.data.model.VideoRequest

object MockChannelData {

    val categories = listOf(
        "All",
        "Tech Tutorials",
        "AI Tools",
        "Stories",
        "Tips",
        "Shorts",
        "Live",
    )

    val videos = listOf(
        VideoItem(
            id = "vid_gemini_bangla",
            youtubeId = "dQw4w9WgXcQ",
            titleBn = "Gemini AI দিয়ে বাংলায় প্রোডাক্টিভিটি ও কোডিং অটোমেশন (সম্পূর্ণ গাইড)",
            titleEn = "Gemini AI Productivity & Coding Automation in Bangla (Complete Guide)",
            descriptionBn = "এই ভিডিওতে দেখানো হয়েছে কীভাবে Gemini 3.8 Flash ব্যবহার করে বাংলায় স্ক্রিপ্ট, কোড ও রিসার্চ দ্রুত করা যায়। 00:00 সূচনা, 02:35 বাংলা প্রম্পটিং, 08:10 অটোমেশন ওয়ার্কফ্লো।",
            descriptionEn = "Learn how to automate scripts, code, and Bangla research using Gemini 3.8 Flash. 00:00 Intro, 02:35 Bangla Prompting, 08:10 Automation Workflow.",
            category = "AI Tools",
            durationSeconds = 945,
            durationLabel = "15:45",
            viewsCount = 128400,
            likesCount = 9420,
            publishedLabelBn = "২ দিন আগে",
            publishedLabelEn = "2 days ago",
            uploadDateDaysAgo = 2,
            tags = listOf("Gemini", "AI Tools", "Bangla Tech", "Automation"),
            chapters = listOf(
                VideoChapter(0, "00:00", "সূচনা ও সেটআপ", "Intro & Setup"),
                VideoChapter(155, "02:35", "বাংলায় সঠিক প্রম্পট লেখার নিয়ম", "Writing Effective Bangla Prompts"),
                VideoChapter(490, "08:10", "ইউটিউব ও কোডিং অটোমেশন", "YouTube & Coding Workflow"),
                VideoChapter(780, "13:00", "ফাইনাল টিপস ও রিসোর্স", "Final Tips & Resources"),
            ),
            resources = listOf(
                ResourceItem(
                    id = "res_1",
                    titleBn = "Gemini বাংলা প্রম্পট চিটশিট (PDF)",
                    titleEn = "Gemini Bangla Prompt Cheatsheet (PDF)",
                    type = "PDF",
                    content = "1. ভূমিকা নির্ধারণ করুন (Act as...)\n2. ভাষা ও টোন উল্লেখ করুন (বাংলা/সহজ)\n3. ধাপে ধাপে আউটপুট চান।",
                    sizeLabel = "840 KB",
                ),
                ResourceItem(
                    id = "res_2",
                    titleBn = "অটো-সামারি সিস্টেম প্রম্পট",
                    titleEn = "Auto-Summary System Prompt",
                    type = "PROMPT",
                    content = "Summarize the following tech tutorial in natural Bangla with 5 bullet points and 1 actionable tip.",
                    sizeLabel = "2 KB",
                ),
            ),
            sampleComments = listOf(
                "Tanvir Hasan" to "ভাই অসাধারণ টিউটোরিয়াল! বাংলায় এমন ক্লিয়ার AI গাইড আর কোথাও পাইনি।",
                "Nusrat Jahan" to "০২:৩৫ এর প্রম্পট ট্রিকটা দারুণ কাজে দিয়েছে!",
            ),
            thumbnailRes = R.drawable.img_ai_lab_card,
        ),
        VideoItem(
            id = "vid_android_compose",
            youtubeId = "M7lc1UVf-VE",
            titleBn = "মোবাইলে AI অ্যাপ বানান — Jetpack Compose ও Material 3 ক্র্যাশ কোর্স",
            titleEn = "Build Modern AI Apps on Android — Jetpack Compose & Material 3 Crash Course",
            descriptionBn = "শুরু থেকে আধুনিক Material 3 অ্যান্ড্রয়েড অ্যাপ ডিজাইন শিখুন। 00:00 আর্কিটেকচার, 05:20 থিম ও কালার, 14:40 নেভিগেশন।",
            descriptionEn = "Master Material 3 Android UI architecture from scratch. 00:00 Architecture, 05:20 Theme & Color, 14:40 Navigation.",
            category = "Tech Tutorials",
            durationSeconds = 1820,
            durationLabel = "30:20",
            viewsCount = 96500,
            likesCount = 7810,
            publishedLabelBn = "৫ দিন আগে",
            publishedLabelEn = "5 days ago",
            uploadDateDaysAgo = 5,
            tags = listOf("Android", "Kotlin", "Jetpack Compose", "Material 3"),
            chapters = listOf(
                VideoChapter(0, "00:00", "প্রজেক্ট আর্কিটেকচার", "Project Architecture"),
                VideoChapter(320, "05:20", "ডিপ ব্লু ও সায়ান থিম সেটআপ", "Deep Blue & Cyan Theme Setup"),
                VideoChapter(880, "14:40", "ডাটা ও ফায়ারবেস কানেকশন", "Data & Firebase Integration"),
            ),
            resources = listOf(
                ResourceItem(
                    id = "res_3",
                    titleBn = "Compose থিম স্টার্টার কোড",
                    titleEn = "Compose Theme Starter Snippet",
                    type = "CODE",
                    content = "MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFF00E5FF)))",
                    sizeLabel = "4 KB",
                ),
            ),
            sampleComments = listOf(
                "Rafiqul Islam" to "Compose দিয়ে প্রথম অ্যাপ বানালাম আপনার ভিডিও দেখে। ধন্যবাদ ভাই!",
            ),
            thumbnailRes = R.drawable.img_hero_tech_lab,
        ),
        VideoItem(
            id = "vid_live_qa",
            youtubeId = "aqz-KE-bpKQ",
            titleBn = "লাইভ: ২০২৬ সালে ফ্রি AI টুলস ও কনটেন্ট ক্রিয়েশন প্রশ্নোত্তর",
            titleEn = "LIVE: 2026 Free AI Tools & Content Creation Q&A",
            descriptionBn = "কবির টেক ল্যাবের সাপ্তাহিক লাইভ আড্ডা ও সরাসরি টেক সমস্যার সমাধান।",
            descriptionEn = "Kabir Tech Lab weekly live Q&A covering AI tools, Android tips, and YouTube growth.",
            category = "Live",
            durationSeconds = 3600,
            durationLabel = "LIVE",
            viewsCount = 41200,
            likesCount = 5200,
            publishedLabelBn = "এখন লাইভ",
            publishedLabelEn = "Live now",
            uploadDateDaysAgo = 0,
            liveStream = true,
            tags = listOf("Live", "Q&A", "AI", "Kabir Tech Lab"),
            thumbnailRes = R.drawable.img_hero_tech_lab,
        ),
        VideoItem(
            id = "vid_premiere_next",
            youtubeId = "jNQXAC9IVRw",
            titleBn = "প্রিমিয়ার: ফোনের ব্যাটারি ও স্পিড দ্বিগুণ করার ১০টি গোপন সেটিংস",
            titleEn = "Premiere: 10 Hidden Android Settings to Boost Battery & Speed",
            descriptionBn = "আগামীকাল রাত ৮টায় প্রিমিয়ার হবে। নোটিফিকেশন অন রাখুন!",
            descriptionEn = "Premiering tomorrow at 8:00 PM BST. Set a reminder!",
            category = "Tips",
            durationSeconds = 720,
            durationLabel = "12:00",
            viewsCount = 14800,
            likesCount = 2100,
            publishedLabelBn = "আগামীকাল রাত ৮:০০",
            publishedLabelEn = "Tomorrow 8:00 PM",
            uploadDateDaysAgo = 0,
            premiereScheduled = true,
            premiereTimeLabel = "18h 30m",
            tags = listOf("Android Tips", "Battery", "Optimization"),
            thumbnailRes = R.drawable.img_ai_lab_card,
        ),
        VideoItem(
            id = "vid_short_1",
            youtubeId = "ysz5S6PUM-U",
            titleBn = "৩০ সেকেন্ডে ফ্রি AI থাম্বনেইল বানানোর ম্যাজিক ট্রিক! #shorts",
            titleEn = "Make Viral AI Thumbnails in 30 Seconds! #shorts",
            descriptionBn = "দ্রুত ও আকর্ষণীয় ইউটিউব থাম্বনেইল তৈরির শর্টকাট।",
            descriptionEn = "Quick workflow to craft high-CTR YouTube thumbnails.",
            category = "Shorts",
            durationSeconds = 48,
            durationLabel = "0:48",
            viewsCount = 312000,
            likesCount = 24900,
            publishedLabelBn = "১ দিন আগে",
            publishedLabelEn = "1 day ago",
            uploadDateDaysAgo = 1,
            shortVideo = true,
            tags = listOf("Shorts", "Thumbnail", "AI"),
            thumbnailRes = R.drawable.img_ai_lab_card,
        ),
        VideoItem(
            id = "vid_short_2",
            youtubeId = "tgbNymZ7vqY",
            titleBn = "এই একটি Google সেটিংস বন্ধ করলে ফোন হবে সুপার ফাস্ট! #shorts",
            titleEn = "Turn Off This One Setting for Instant Android Speed! #shorts",
            descriptionBn = "অপ্রয়োজনীয় ব্যাকগ্রাউন্ড স্ক্যানিং বন্ধ করার সহজ উপায়।",
            descriptionEn = "Simple background scan tweak for smoother performance.",
            category = "Shorts",
            durationSeconds = 55,
            durationLabel = "0:55",
            viewsCount = 198400,
            likesCount = 18300,
            publishedLabelBn = "৩ দিন আগে",
            publishedLabelEn = "3 days ago",
            uploadDateDaysAgo = 3,
            shortVideo = true,
            tags = listOf("Shorts", "Android", "Speed"),
            thumbnailRes = R.drawable.img_hero_tech_lab,
        ),
    )

    val stories = listOf(
        StorySeries(
            id = "story_brishti_chithi",
            titleBn = "বৃষ্টি ভেজা শেষ চিঠি (পর্ব ১–১০)",
            titleEn = "The Rain-Soaked Final Letter (Parts 1–10)",
            genre = "Romantic",
            synopsisBn = "ঢাকার এক বর্ষণমুখর সন্ধ্যায় হারিয়ে যাওয়া দুটি মানুষের না বলা ভালোবাসা ও অপেক্ষার হৃদয়স্পর্শী বাংলা ধারাবাহিক গল্প।",
            synopsisEn = "A bittersweet Bangla love story about two souls separated on a rainy Dhaka evening, bound by unspoken letters.",
            previouslyOnBn = "আগের পর্বে: অর্ণব পুরনো বইয়ের আলমারিতে মেঘলার লেখা প্রথম নীল খামটি খুঁজে পায়, যেখানে কমলাপুর স্টেশনের শেষ ট্রেনের সময় লেখা ছিল...",
            previouslyOnEn = "Previously on: Arnab discovers Meghla's hidden blue envelope inside an old bookshelf with the timestamp of the last train at Kamalapur...",
            storyOfTheWeek = true,
            coverRes = R.drawable.img_story_rainy_dhaka,
            characters = listOf(
                StoryCharacter("অর্ণব", "Arnab", "অন্তর্মুখী সফটওয়্যার ইঞ্জিনিয়ার", "Introverted software engineer"),
                StoryCharacter("মেঘলা", "Meghla", "রেডিও জকি ও গল্পকার", "Late-night radio host & writer"),
                StoryCharacter("সাদমান", "Sadman", "অর্ণবের পুরনো বন্ধু", "Arnab's childhood friend"),
            ),
            parts = (1..10).map { part ->
                StoryPart(
                    id = "brishti_part_$part",
                    partNumber = part,
                    titleBn = "পর্ব $part: " + when (part) {
                        1 -> "নীল খামের ঠিকানা"
                        2 -> "রাত দশটার স্টেশন"
                        3 -> "বৃষ্টির শহরের কফি শপ"
                        4 -> "হারানো ডায়েরির পাতা"
                        5 -> "নিঃশব্দ অপেক্ষা"
                        6 -> "একটি অচেনা ফোন কল"
                        7 -> "পুরনো চিলেকোঠা"
                        8 -> "ভুল বোঝাবুঝির দেয়াল"
                        9 -> "শেষ ট্রেনের বাঁশি"
                        else -> "বৃষ্টি শেষে রোদ্দুর"
                    },
                    titleEn = "Part $part: " + when (part) {
                        1 -> "Address on the Blue Envelope"
                        2 -> "Ten O'Clock Station"
                        3 -> "Coffee Shop in the Rain"
                        4 -> "Pages of a Lost Diary"
                        5 -> "Silent Waiting"
                        6 -> "An Unknown Phone Call"
                        7 -> "The Old Rooftop Room"
                        8 -> "Wall of Misunderstandings"
                        9 -> "Whistle of the Last Train"
                        else -> "Sunlight After the Rain"
                    },
                    durationSeconds = 1800,
                    durationLabel = "30:00",
                    youtubeId = "dQw4w9WgXcQ",
                    storyTextBn = "রাত তখন প্রায় সাড়ে এগারোটা। শহরের ব্যস্ত রাস্তাগুলোতে বৃষ্টির ফোঁটা তখনো থামেনি। ল্যাম্পপোস্টের হালকা হলুদ আলো ভিজে থাকা পিচঢালা রাস্তায় অদ্ভুত এক মায়া তৈরি করেছিল।\n\nঅর্ণব জানালার পাশে বসে চায়ের কাপটা হাতে নিয়ে ভাবছিল মেঘলার শেষ কথাগুলোর মানে। 'মানুষ যাকে সবচেয়ে বেশি ভালোবাসে, তাকেই কখনো কখনো সবচেয়ে বেশি দূরে ঠেলে দেয় — শুধু তাকে ভালো রাখার জন্য।' ডায়েরির এই লাইনটি আজও তার কানে বাজে।\n\nটেবিলের ওপর রাখা পুরনো নীল খামটা আবারও হাতে নিল সে। খামের কোনায় বৃষ্টির দাগ এখনো স্পষ্ট। সে জানে না আগামীকাল স্টেশনের প্ল্যাটফর্মে মেঘলা সত্যিই আসবে কি না, তবুও মন বলছে এই গল্প এখানেই শেষ হওয়ার নয়...",
                    storyTextEn = "It was nearly half past eleven at night. Raindrops still tapped gently against the quiet streets of Dhaka, casting golden reflections beneath the old streetlamps.\n\nSitting by the window with a warm cup of tea, Arnab replayed Meghla's final words in his mind: 'Sometimes we let go of the ones we love most, just to protect their peace.'\n\nHe picked up the weathered blue envelope resting on the desk. The rain marks at the corner were still faint yet unmistakable. Tomorrow at the station, everything would finally be answered...",
                    rating = 4.9f,
                    reactionsCount = 1200 + part * 140,
                )
            }
        ),
        StorySeries(
            id = "story_akash_opare",
            titleBn = "নীল আকাশের ওপারে (পর্ব ১–৬)",
            titleEn = "Beyond the Blue Horizon (Parts 1–6)",
            genre = "Sad",
            synopsisBn = "একজন সংগ্রামী বাবার ত্যাগ এবং প্রবাস জীবনের নীরব কষ্ট নিয়ে লেখা আবেগঘন পারিবারিক গল্প।",
            synopsisEn = "An emotional family drama depicting a father's quiet sacrifices and hope across borders.",
            previouslyOnBn = "আগের পর্বে: ছোট্ট রাফির পরীক্ষার ফলাফলের খবর শুনে দূর প্রবাস থেকে বাবা আনন্দে কেঁদে ফেলেন...",
            previouslyOnEn = "Previously on: Hearing about little Rafi's exam results, his father breaks into tears of pride from miles away...",
            storyOfTheWeek = false,
            coverRes = R.drawable.img_hero_tech_lab,
            characters = listOf(
                StoryCharacter("করিম সাহেব", "Karim Mia", "স্নেহময় পিতা", "Devoted father"),
                StoryCharacter("রাফি", "Rafi", "বিশ্ববিদ্যালয়ের ছাত্র", "University student"),
            ),
            parts = (1..6).map { part ->
                StoryPart(
                    id = "akash_part_$part",
                    partNumber = part,
                    titleBn = "পর্ব $part: জীবনের নীরব অধ্যায়",
                    titleEn = "Part $part: Silent Chapters of Life",
                    durationSeconds = 1650,
                    durationLabel = "27:30",
                    youtubeId = "M7lc1UVf-VE",
                    storyTextBn = "সকালের প্রথম আলো যখন গ্রামের কাঁচা রাস্তায় এসে পড়ত, করিম সাহেব তখনো চিঠির অপেক্ষায় থাকতেন। শহরের ব্যস্ততার মাঝে মানুষ কত সহজে ভুলে যায় পেছনের গল্পগুলো, কিন্তু মাটির গন্ধ আর বাবার হাতের স্পর্শ কখনো মুছে যায় না।",
                    storyTextEn = "When the first morning light touched the village path, Karim still waited patiently by the gate for a call from the city. Time moves fast, yet the warmth of sacrifice endures.",
                    rating = 4.8f,
                    reactionsCount = 890 + part * 90,
                )
            }
        ),
        StorySeries(
            id = "story_ai_detective",
            titleBn = "প্রজেক্ট সাইবার-৭: অদৃশ্য সংকেত (থ্রিলার)",
            titleEn = "Project Cyber-7: The Hidden Signal (Thriller)",
            genre = "Thriller",
            synopsisBn = "একজন এথিক্যাল হ্যাকার ও একটি রহস্যময় এআই ল্যাবের রোমাঞ্চকর সাই-ফাই থ্রিলার।",
            synopsisEn = "A gripping Bangla tech thriller about an ethical hacker uncovering a rogue AI signal.",
            previouslyOnBn = "আগের পর্বে: সার্ভার রুমের লগ ফাইলে পাওয়া যায় রাত ৩:১৪ মিনিটের অজানা এনক্রিপ্টেড মেসেজ...",
            previouslyOnEn = "Previously on: Server logs reveal an encrypted 3:14 AM broadcast originating from inside the lab...",
            storyOfTheWeek = false,
            coverRes = R.drawable.img_ai_lab_card,
            characters = listOf(
                StoryCharacter("নাবিল", "Nabil", "সাইবার সিকিউরিটি বিশেষজ্ঞ", "Cybersecurity researcher"),
                StoryCharacter("নোভা-৯", "Nova-9", "রহস্যময় এআই সত্তা", "Mysterious AI core"),
            ),
            parts = (1..5).map { part ->
                StoryPart(
                    id = "thriller_part_$part",
                    partNumber = part,
                    titleBn = "পর্ব $part: এনক্রিপ্টেড সংকেত",
                    titleEn = "Part $part: Encrypted Signal",
                    durationSeconds = 1500,
                    durationLabel = "25:00",
                    youtubeId = "ysz5S6PUM-U",
                    storyTextBn = "মনিটরের নীল আলোয় নাবিলের চোখ স্থির। প্রতিটি বাইটের ভেতর লুকিয়ে আছে একটি সংকেত—কেউ একজন ভেতর থেকেই পুরো সিস্টেম নিয়ন্ত্রণ করছে।",
                    storyTextEn = "Nabil stared at the glowing terminal. Hidden inside every packet was a signature from inside the vault itself.",
                    rating = 4.9f,
                    reactionsCount = 1120 + part * 110,
                )
            }
        ),
    )

    val curatedPlaylists = listOf(
        CuratedPlaylist(
            id = "pl_ai_mastery",
            titleBn = "Gemini ও এআই টুলস মাস্টারি সিরিজ",
            titleEn = "Gemini & AI Tools Mastery Series",
            descriptionBn = "শূন্য থেকে প্রো লেভেল পর্যন্ত AI অটোমেশন ও প্রম্পট ইঞ্জিনিয়ারিং।",
            descriptionEn = "Go from zero to pro in AI automation and prompt engineering.",
            category = "AI Tools",
            videoIds = listOf("vid_gemini_bangla", "vid_short_1", "vid_live_qa"),
            coverRes = R.drawable.img_ai_lab_card,
        ),
        CuratedPlaylist(
            id = "pl_android_dev",
            titleBn = "অ্যান্ড্রয়েড অ্যাপ ডেভেলপমেন্ট ও টিপস",
            titleEn = "Android App Dev & Pro Tips",
            descriptionBn = "Compose, Kotlin এবং স্মার্টফোন অপটিমাইজেশন সম্পূর্ণ সিরিজ।",
            descriptionEn = "Complete Compose, Kotlin, and smartphone optimization guides.",
            category = "Tech Tutorials",
            videoIds = listOf("vid_android_compose", "vid_premiere_next", "vid_short_2"),
            coverRes = R.drawable.img_hero_tech_lab,
        ),
    )

    val toolsDirectory = listOf(
        ToolDirectoryItem("tool_1", "Gemini AI Studio", "AI Tools", "প্রম্পট টেস্টিং ও অ্যাপ বিল্ডিং প্ল্যাটফর্ম", "Prompt prototyping & app building workspace", "https://aistudio.google.com"),
        ToolDirectoryItem("tool_2", "CapCut Mobile", "Editing", "সহজ বাংলা সাবটাইটেল ও শর্টস এডিটিং", "Fast mobile video & auto-caption editing", "https://www.capcut.com"),
        ToolDirectoryItem("tool_3", "Canva", "Design", "ইউটিউব থাম্বনেইল ও সোশ্যাল ব্যানার ডিজাইন", "YouTube thumbnail & banner design studio", "https://www.canva.com"),
        ToolDirectoryItem("tool_4", "Firebase Hosting", "Hosting", "দ্রুত ফ্রি ওয়েব ও অ্যাপ ব্যাকএন্ড হোস্টিং", "Fast serverless app & static hosting", "https://firebase.google.com"),
        ToolDirectoryItem("tool_5", "OBS Studio", "Apps", "পিসি স্ক্রিন রেকর্ডিং ও ইউটিউব লাইভ স্ট্রিমিং", "Open-source screen capture & live streaming", "https://obsproject.com"),
        ToolDirectoryItem("tool_6", "Photopea", "Websites", "ব্রাউজারে ফ্রি ফটোশপ অল্টারনেটিভ", "Free browser-based PSD & thumbnail editor", "https://www.photopea.com"),
    )

    val promptLibrary = listOf(
        PromptTemplate(
            id = "prm_1",
            titleBn = "বাংলা টেক ভিডিও স্ক্রিপ্ট প্রম্পট",
            titleEn = "Bangla Tech Video Script Prompt",
            category = "Script",
            promptText = "Write an engaging 8-minute YouTube script in natural spoken Bangla about [TOPIC]. Include a 15-second curiosity hook, 3 clear sections, and a warm call-to-action for Kabir Tech Lab.",
            rating = 4.9f,
        ),
        PromptTemplate(
            id = "prm_2",
            titleBn = "হাই-সিটিআর থাম্বনেইল কনসেপ্ট প্রম্পট",
            titleEn = "High-CTR Thumbnail Concept Prompt",
            category = "Thumbnail",
            promptText = "Generate 5 high-contrast YouTube thumbnail concepts for '[TITLE]'. Suggest a 2-3 word Bangla text overlay, facial expression, and deep blue + electric cyan color contrast.",
            rating = 4.8f,
        ),
        PromptTemplate(
            id = "prm_3",
            titleBn = "আবেগঘন বাংলা গল্প ভয়েসওভার স্টাইল",
            titleEn = "Emotional Bangla Story Voiceover Script",
            category = "Voiceover",
            promptText = "Convert this short outline into a slow-paced, poetic Bangla audio drama narration with subtle pause cues [pause 1s] for midnight listeners.",
            rating = 4.9f,
        ),
        PromptTemplate(
            id = "prm_4",
            titleBn = "অ্যান্ড্রয়েড অ্যাপ ফিচার প্ল্যানার",
            titleEn = "Android App Feature Spec Builder",
            category = "App Building",
            promptText = "Design an MVP architecture in Kotlin & Jetpack Compose for [APP IDEA], including Material 3 screens, offline cache, and Firebase Firestore collections.",
            rating = 4.7f,
        ),
    )

    val courseLessons = listOf(
        CourseLesson("les_1", "Beginner", "পাঠ ১: এআই প্রম্পটিং-এর মূল ভিত্তি", "Lesson 1: Foundations of AI Prompting", "কীভাবে স্পষ্ট নির্দেশনা দিয়ে সেরা ফলাফল পাবেন।", "Structure clear context, role, and format.", "10 min"),
        CourseLesson("les_2", "Beginner", "পাঠ ২: স্মার্টফোনে ইউটিউব চ্যানেল সেটআপ", "Lesson 2: Mobile-First YouTube Studio Setup", "সঠিক নাম, ব্যানার, এবং এসইও ট্যাগ।", "Branding, banners, and Bangla SEO tags.", "14 min"),
        CourseLesson("les_3", "Intermediate", "পাঠ ৩: বাংলা গল্পের অডিও মিক্সিং", "Lesson 3: Atmospheric Audio Mixing for Stories", "ব্যাকগ্রাউন্ড মিউজিক ও ভয়েস ব্যালেন্স।", "Voice EQ, rain ambience, and pacing.", "18 min"),
        CourseLesson("les_4", "Advanced", "পাঠ ৪: Gemini + Kotlin অ্যাপ ইন্টিগ্রেশন", "Lesson 4: Gemini + Kotlin App Integration", "অ্যাপে রিয়েল-টাইম AI চ্যাট ও স্ট্রিমিং যুক্ত করা।", "Stream generative responses with timeouts.", "25 min"),
    )

    val glossaryTerms = listOf(
        GlossaryTerm("gl_1", "Prompt Engineering", "এআই মডেল থেকে নির্ভুল উত্তর পেতে সঠিক নির্দেশনা লেখার কৌশল।", "Crafting structured inputs to guide AI model outputs accurately."),
        GlossaryTerm("gl_2", "API Quota", "একটি নির্দিষ্ট সময়ে সার্ভার থেকে সর্বোচ্চ কতবার ডাটা আনা যাবে তার সীমা।", "Rate and daily request limit enforced by an API provider."),
        GlossaryTerm("gl_3", "Streaming (TTFT)", "পুরো উত্তর শেষ হওয়ার আগে শব্দে শব্দে দ্রুত উত্তর দেখানোর প্রযুক্তি।", "Incremental token delivery that cuts Time-to-First-Token."),
        GlossaryTerm("gl_4", "Jetpack Compose", "অ্যান্ড্রয়েডে কটলিন দিয়ে আধুনিক UI তৈরির আধুনিক ডিক্লেয়ারেটিভ টুলকিট।", "Android's modern declarative UI toolkit built in Kotlin."),
    )

    val quizQuestions = listOf(
        QuizQuestion(
            id = "q_1",
            category = "AI",
            difficulty = "Easy",
            questionBn = "Gemini থেকে সরাসরি ক্লিন JSON ডাটা পেতে কোন কনফিগারেশন ব্যবহার করা হয়?",
            questionEn = "Which configuration ensures Gemini returns clean structured JSON?",
            optionsBn = listOf("responseSchema ও application/json", "শুধু বড় ফন্ট", "JPEG কম্প্রেশন", "অডিও ফিল্টার"),
            optionsEn = listOf("responseSchema & application/json", "Large font only", "JPEG compression", "Audio filter"),
            correctIndex = 0,
            explanationBn = "responseMimeType = \"application/json\" এবং responseSchema দিলে মডেল সরাসরি ভ্যালিড JSON দেয়।",
            explanationEn = "Setting responseMimeType and responseSchema guarantees strict JSON output.",
        ),
        QuizQuestion(
            id = "q_2",
            category = "Android",
            difficulty = "Medium",
            questionBn = "Android-এ মিডিয়া নির্বাচনের জন্য Google Play-এর অনুমোদিত ও নিরাপদ পদ্ধতি কোনটি?",
            questionEn = "Which zero-permission method is recommended on Android for picking photos?",
            optionsBn = listOf("Photo Picker (PickVisualMedia)", "READ_EXTERNAL_STORAGE", "Root Access", "Manual File Path"),
            optionsEn = listOf("Photo Picker (PickVisualMedia)", "READ_EXTERNAL_STORAGE", "Root Access", "Manual File Path"),
            correctIndex = 0,
            explanationBn = "PickVisualMedia কোনো স্টোরেজ পারমিশন ছাড়াই নিরাপদভাবে ছবি নির্বাচন করতে দেয়।",
            explanationEn = "Android Photo Picker works securely without requesting broad storage permissions.",
        ),
        QuizQuestion(
            id = "q_3",
            category = "YouTube",
            difficulty = "Easy",
            questionBn = "YouTube ভিডিওর প্রথম ১৫ সেকেন্ডকে কী বলা হয় যা দর্শককে ধরে রাখে?",
            questionEn = "What are the opening 15 seconds of a YouTube video designed to retain viewers called?",
            optionsBn = listOf("Hook (হুক)", "End Screen", "Watermark", "Metadata"),
            optionsEn = listOf("The Hook", "End Screen", "Watermark", "Metadata"),
            correctIndex = 0,
            explanationBn = "প্রথম ১৫ সেকেন্ডের আকর্ষণীয় শুরুকে Hook বলা হয়, যা Audience Retention বাড়ায়।",
            explanationEn = "A strong opening Hook captures curiosity and boosts early audience retention.",
        ),
    )

    val flashcards = listOf(
        FlashcardItem("fc_1", "RAG (Retrieval-Augmented Generation)", "নিজস্ব ডকুমেন্ট বা ডাটাবেজ থেকে তথ্য খুঁজে এআই উত্তর নির্ভুল করার পদ্ধতি।", "Grounding LLM answers in retrieved external documents.", "AI"),
        FlashcardItem("fc_2", "StateFlow", "কটলিন কো-রুটিনে UI স্টেট ধারণ ও রিয়েল-টাইম আপডেটের জন্য হট স্ট্রিম।", "Hot observable state holder stream in Kotlin Coroutines.", "Android"),
        FlashcardItem("fc_3", "CTR (Click-Through Rate)", "থাম্বনেইল দেখার পর কত শতাংশ দর্শক ভিডিওতে ক্লিক করেছে তার হার।", "Percentage of impressions that convert into video clicks.", "YouTube"),
    )

    val samplePolls = listOf(
        PollItem(
            id = "poll_1",
            questionBn = "পরবর্তী শুক্রবার কোন ভিডিও বা গল্পটি দেখতে চান?",
            questionEn = "Which upload should go live next Friday?",
            options = listOf(
                PollOption("opt_1", "ফ্রি AI এজেন্ট তৈরির বাংলা গাইড", "Build Free AI Agents in Bangla", 342),
                PollOption("opt_2", "নতুন ধারাবাহিক রোমান্টিক গল্প", "New Romantic Bangla Audio Story", 418),
                PollOption("opt_3", "Android প্রোফাইল ও প্রাইভেসি সিকিউরিটি", "Android Privacy & Security Masterclass", 196),
            )
        )
    )

    val sampleEvents = listOf(
        CommunityEvent("evt_1", "সাপ্তাহিক AI ও টেক লাইভ আড্ডা", "Weekly AI & Tech Live Q&A", "Friday • 8:30 PM BST", "YouTube Live"),
        CommunityEvent("evt_2", "গল্পের আসর: বৃষ্টি ভেজা শেষ চিঠি ফিনালে", "Story Premiere: Final Chapter", "Sunday • 10:00 PM BST", "Audio Premiere"),
    )

    val sampleNotifications = listOf(
        NotificationItem("n_1", "নতুন AI টিউটোরিয়াল প্রকাশিত!", "New AI Tutorial Live!", "Gemini AI দিয়ে বাংলায় কোডিং অটোমেশন এখন দেখুন।", "Watch Gemini AI Coding Automation now.", "10m ago", "videos"),
        NotificationItem("n_2", "সাপ্তাহিক গল্পের নতুন পর্ব", "New Story Chapter Available", "বৃষ্টি ভেজা শেষ চিঠি — পর্ব ১০ যোগ করা হয়েছে।", "The Rain-Soaked Final Letter Part 10 is out.", "2h ago", "stories"),
        NotificationItem("n_3", "দৈনিক কুইজ রিমাইন্ডার", "Daily Quiz Streak Reminder", "আজকের টেক কুইজ খেলে +২৫ XP জিতে নিন!", "Play today's tech quiz to keep your streak!", "5h ago", "quiz"),
    )

    val sampleAnnouncements = listOf(
        Announcement(
            id = "ann_pinned_1",
            authorId = "system",
            titleBn = "কবির টেক ল্যাব কমিউনিটিতে স্বাগতম!",
            titleEn = "Welcome to Kabir Tech Lab!",
            bodyBn = "প্রতি শুক্রবার টেক টিউটোরিয়াল এবং রবিবার রাত ১০টায় নতুন বাংলা গল্প শুনুন।",
            bodyEn = "New Tech Tutorials every Friday and Bangla Audio Stories every Sunday at 10 PM.",
            pinned = true,
            visibility = "PUBLIC",
        )
    )

    val sampleCommunityPosts = listOf(
        CommunityPost(
            id = "post_demo_1",
            authorId = "creator_kabir",
            authorName = "Kabir Tech Lab",
            postType = "FEED",
            title = "নতুন Gemini 3.8 প্রম্পট প্যাক কেমন লাগছে?",
            body = "অ্যাপের Resources হাব থেকে আজকের ফ্রি বাংলা প্রম্পটগুলো ডাউনলোড করে জানাবেন!",
            likesCount = 184,
            repliesCount = 24,
            approved = true,
            reported = false,
        ),
        CommunityPost(
            id = "post_demo_2",
            authorId = "member_sadia",
            authorName = "Sadia Afrin",
            postType = "QA",
            title = "বৃষ্টি ভেজা শেষ চিঠি গল্পের ব্যাকগ্রাউন্ড মিউজিক কীভাবে বানানো হয়েছে?",
            body = "রাতের বৃষ্টির শব্দ আর সফট পিয়ানোটা অসাধারণ লেগেছে। কোন টুল ব্যবহার করা হয়েছে?",
            likesCount = 67,
            repliesCount = 9,
            approved = true,
            reported = false,
        ),
        CommunityPost(
            id = "post_demo_3",
            authorId = "member_tamim",
            authorName = "Tamim Chowdhury",
            postType = "SHOWCASE",
            title = "আপনার টিউটোরিয়াল দেখে আমার প্রথম বাংলা নোট অ্যাপ বানালাম!",
            body = "Jetpack Compose আর Material 3 ডার্ক থিম ব্যবহার করে বানিয়েছি। সবার ফিডব্যাক চাই।",
            likesCount = 112,
            repliesCount = 15,
            approved = true,
            reported = false,
        ),
    )

    val sampleVideoRequests = listOf(
        VideoRequest(
            id = "req_demo_1",
            userId = "member_farhan",
            authorName = "Farhan Labib",
            title = "AI দিয়ে পুরো বাংলা অডিওবুক এডিটিং টিউটোরিয়াল",
            category = "AI Tools",
            description = "ভয়েস ক্লিনআপ, অটো মিউজিক ডাকেং ও থাম্বনেইল বানানোর সম্পূর্ণ প্রসেস।",
            upvotes = 94,
            status = "PLANNED",
            visibility = "PUBLIC",
        ),
        VideoRequest(
            id = "req_demo_2",
            userId = "member_mehedi",
            authorName = "Mehedi Hasan",
            title = "ফোন হ্যাং হলে র‍্যাম ছাড়াই ফাস্ট করার প্রো গাইড",
            category = "Tech Tutorials",
            description = "ADB ও ডেভেলপার অপশন ব্যবহার করে অপ্রয়োজনীয় প্রসেস বন্ধ করা।",
            upvotes = 62,
            status = "IN_PROGRESS",
            visibility = "PUBLIC",
        ),
    )

    val sampleLeaderboard = listOf(
        Triple("Sadia Afrin", 2450, 14),
        Triple("Tamim Chowdhury", 2180, 11),
        Triple("Nusrat Jahan", 1920, 9),
        Triple("Farhan Labib", 1640, 7),
        Triple("Mehedi Hasan", 1410, 6),
    )
}
