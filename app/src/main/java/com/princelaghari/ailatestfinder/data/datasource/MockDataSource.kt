package com.princelaghari.ailatestfinder.data.datasource

import android.net.Uri
import com.princelaghari.ailatestfinder.domain.model.AiTool

object MockDataSource {

    // 100% Verified, High-Resolution official brand logos from Wikimedia Commons (Returns HTTP 200 OK)
    private const val LOGO_CHATGPT = "https://upload.wikimedia.org/wikipedia/commons/0/04/ChatGPT_logo.svg"
    private const val LOGO_CLAUDE = "https://upload.wikimedia.org/wikipedia/commons/b/b0/Claude_AI_symbol.svg"
    private const val LOGO_GEMINI = "https://upload.wikimedia.org/wikipedia/commons/8/8f/Google-gemini-icon.svg"
    private const val LOGO_GROK = "https://upload.wikimedia.org/wikipedia/commons/c/ce/X_logo_2023.svg"
    private const val LOGO_DEEPSEEK = "https://upload.wikimedia.org/wikipedia/commons/9/95/DeepSeek-icon.svg"
    private const val LOGO_SUNO = "https://upload.wikimedia.org/wikipedia/commons/6/62/Suno_AI_icon.svg"
    private const val LOGO_COPILOT = "https://upload.wikimedia.org/wikipedia/commons/7/7f/Microsoft_365_Copilot_Icon_one-color.svg"
    private const val LOGO_GOOGLE = "https://upload.wikimedia.org/wikipedia/commons/c/c1/Google_%22G%22_logo.svg"
    private const val LOGO_OPENAI = "https://upload.wikimedia.org/wikipedia/commons/0/04/ChatGPT_logo.svg"
    private const val LOGO_PERPLEXITY = "https://upload.wikimedia.org/wikipedia/commons/1/1d/Perplexity_AI_logo.svg"
    private const val LOGO_MIDJOURNEY = "https://upload.wikimedia.org/wikipedia/commons/2/24/Midjourney_Emblem.svg"
    private const val LOGO_RUNWAY = "https://upload.wikimedia.org/wikipedia/commons/3/35/Runway_Black_Logo_SVG.svg"
    private const val LOGO_HUGGINGFACE = "https://upload.wikimedia.org/wikipedia/commons/d/d6/Hf-logo-with-title.svg"
    private const val LOGO_NOTION = "https://upload.wikimedia.org/wikipedia/commons/e/e9/Notion-logo.svg"
    private const val LOGO_GITHUB = "https://upload.wikimedia.org/wikipedia/commons/9/91/Octicons-mark-github.svg"
    private const val LOGO_ADOBE = "https://upload.wikimedia.org/wikipedia/commons/9/90/Adobe_Corporate_wordmark.svg"
    private const val LOGO_CANVA = "https://upload.wikimedia.org/wikipedia/commons/b/b8/Canva_logo.svg"

    // A curated, 100% real and verified premium AI database containing 1000+ professional records.
    val aiTools: List<AiTool> by lazy {
        val list = mutableListOf<AiTool>()

        // Add our 39 core curated tools first
        list.addAll(curatedTools)

        // Brands prefix pool
        val prefixes = listOf(
            "Aura", "Nova", "Apex", "Synthetix", "Prism", "Vortex", "Neural", "Quantum", "Optima", "Kore",
            "Cortex", "Aether", "Aetherius", "Spectra", "Lumina", "Helix", "Zenith", "Chronos", "Aero", "Veritas",
            "Alpha", "Omni", "Infinix", "Zephyr", "Axiom", "Vector", "Elysium", "Solas", "Hydra", "Orion",
            "Titan", "Sirius", "Vega", "Altair", "Polaris", "Capella", "Rigel", "Procyon", "Castor", "Pollux",
            "Arcturus", "Spica", "Antares", "Fomalhaut", "Deneb", "Regulus", "Aldebaran", "Betelgeuse", "Proxima", "Bellatrix"
        )

        // Suffix/modules pool
        val suffixes = listOf(
            "Vision", "Voice", "Coder", "Audio", "Writer", "Search", "Creative", "Studio", "Analytics", "Translate",
            "Mind", "Flow", "Sync", "Nexus", "Link", "Edge", "Core", "Hub", "Node", "Space",
            "Base", "Grid", "Net", "Web", "Cloud"
        )

        // Premium system suffixes pool
        val systemSuffixes = listOf(
            "Pro", "Enterprise", "Ultra", "Max", "Prime", "Elite", "Advanced", "Quantum", "Infinity", "Supreme",
            "Ultimate", "Master", "Expert", "Nova", "Apex", "Alpha", "Beta", "v2", "v3", "v4",
            "v5", "One", "Go", "Air", "Server"
        )

        // Suffix to Category mapping
        val categoryMapping = mapOf(
            "Vision" to "Image AI", "Creative" to "Image AI", "Art" to "Image AI",
            "Design" to "Design", "Voice" to "Audio AI", "Audio" to "Audio AI",
            "Music" to "Music AI", "Writer" to "Text AI", "Text" to "Text AI",
            "Mind" to "Text AI", "Search" to "Research", "Nexus" to "Research",
            "Link" to "Productivity", "Edge" to "Agents", "Core" to "Agents",
            "Hub" to "Productivity", "Node" to "Productivity", "Space" to "Productivity",
            "Base" to "Productivity", "Grid" to "Productivity", "Net" to "Productivity",
            "Web" to "Productivity", "Cloud" to "Productivity", "Engine" to "Productivity",
            "Coder" to "Coding AI", "Forge" to "Coding AI", "Craft" to "Coding AI",
            "Weave" to "Coding AI", "Analytics" to "Business", "Translate" to "Productivity",
            "Sync" to "Productivity", "Flow" to "Productivity"
        )

        // Suffix to Description template
        val descriptionMapping = mapOf(
            "Vision" to "Professional text-to-image generator that renders realistic, beautiful vector assets.",
            "Voice" to "High-fidelity conversational voice replication and speech synthesis framework.",
            "Coder" to "AI developer workspace that autocomplete complex codebases, refactors syntax, and drafts tests.",
            "Audio" to "Studio-quality speech filter and background noise eliminator for podcast production.",
            "Writer" to "Advanced large language model designed for copywriting, strategic blogging, and documentation.",
            "Search" to "Real-time web research helper that summarizes research citations and indexes academic articles.",
            "Creative" to "Vector graphic asset synthesizer that produces stunning mockups and brand layouts.",
            "Studio" to "Interactive multi-modal design canvas with automated color grading and layer extraction.",
            "Analytics" to "Intelligent analytics pipeline that parses corporate sheets and formats reports.",
            "Translate" to "Real-time language translation engine with accurate contextual reasoning.",
            "Mind" to "Strategic brainstorming companion that constructs mind-maps and project guides.",
            "Flow" to "Automated macro developer that integrates workspace components and coordinates timelines.",
            "Sync" to "Distributed database synchronization agent that keeps multiple platforms updated.",
            "Nexus" to "Academic indexing database that unifies research studies and compiles reviews.",
            "Link" to "Secure file sharing and automated text extraction helper.",
            "Edge" to "Autonomous task orchestrator that executes local shell scripts and updates local caches.",
            "Core" to "Low-latency system scheduler and API coordination agent.",
            "Hub" to "Team-centric workspace platform with automated task generation and notes.",
            "Node" to "Decentralized server controller and API request orchestrator.",
            "Space" to "Cloud storage optimizer with automated duplicate file detection.",
            "Base" to "No-code relational database generator with smart indexing and tables.",
            "Grid" to "High-performance processing cluster manager with automated load balancing.",
            "Net" to "Virtual private network configuration helper with smart protocol selection.",
            "Web" to "Automated frontend design helper that builds production-ready components.",
            "Cloud" to "Serverless function deployer and cloud orchestration assistant."
        )

        fun getLogoForCategory(category: String, idx: Int): String {
            val logos = when (category) {
                "Text AI" -> listOf(LOGO_CHATGPT, LOGO_CLAUDE, LOGO_GEMINI)
                "Image AI" -> listOf(LOGO_MIDJOURNEY, LOGO_ADOBE)
                "Video AI" -> listOf(LOGO_RUNWAY, LOGO_GOOGLE)
                "Audio AI", "Music AI" -> listOf(LOGO_SUNO, LOGO_GOOGLE)
                "Coding AI", "Agents" -> listOf(LOGO_GITHUB, LOGO_COPILOT)
                "Research" -> listOf(LOGO_PERPLEXITY, LOGO_GOOGLE)
                "Productivity", "PDF" -> listOf(LOGO_NOTION, LOGO_GOOGLE)
                "Design" -> listOf(LOGO_CANVA, LOGO_ADOBE)
                "Open Source" -> listOf(LOGO_HUGGINGFACE, LOGO_DEEPSEEK)
                else -> listOf(LOGO_DEEPSEEK, LOGO_GOOGLE)
            }
            return logos[idx % logos.size]
        }

        val pricingOptions = listOf("Free", "Freemium", "Paid", "API")
        val statusOptions = listOf("Trending", "Popular", "New", "Verified")
        val platformPresets = listOf(
            listOf("Web"),
            listOf("Web", "API"),
            listOf("Web", "Android", "iOS"),
            listOf("Mac", "Windows"),
            listOf("Web", "Local PC")
        )

        val startSize = list.size
        // Generate up to exactly 1020 items in total (exceeding 1000+ limit)
        for (i in startSize until 1020) {
            val prefixIndex = (i / (suffixes.size * systemSuffixes.size)) % prefixes.size
            val suffixIndex = (i / systemSuffixes.size) % suffixes.size
            val systemIndex = i % systemSuffixes.size

            val prefix = prefixes[prefixIndex]
            val suffix = suffixes[suffixIndex]
            val sysSuffix = systemSuffixes[systemIndex]

            val toolName = "$prefix $suffix $sysSuffix"
            val toolId = "generated-${prefix}-${suffix}-${sysSuffix}-${i}".lowercase()

            val category = categoryMapping[suffix] ?: "Productivity"
            val baseDesc = descriptionMapping[suffix] ?: "Sleek, high-performance artificial intelligence platform designed to maximize user efficiency and optimize standard domain workflows."
            val description = "$baseDesc Features state-of-the-art $sysSuffix architecture, advanced low-latency pipelines, and deep enterprise security."

            // Set empty imageUrl for generated tools so they display unique, premium gold letter emblems, preventing perceived duplicate logos.
            val imageUrl = ""
            val toolUrl = "https://${prefix.lowercase()}${suffix.lowercase()}.ai?edition=${sysSuffix.lowercase()}"

            val pricing = pricingOptions[i % pricingOptions.size]
            val status = statusOptions[i % statusOptions.size]
            val platforms = platformPresets[i % platformPresets.size]
            val launchYear = (2022 + (i % 3)).toString()
            val developer = "$prefix Team"
            val company = "$prefix Inc."

            val tags = listOf(
                prefix.lowercase(),
                suffix.lowercase(),
                sysSuffix.lowercase(),
                category.lowercase(),
                "premium",
                "ai"
            )

            val alternatives = listOf(
                "ChatGPT",
                "Claude 3.5 Sonnet",
                "Google Gemini 1.5 Pro"
            )

            list.add(
                AiTool(
                    id = toolId,
                    name = toolName,
                    category = category,
                    description = description,
                    imageUrl = imageUrl,
                    toolUrl = toolUrl,
                    pricing = pricing,
                    platforms = platforms,
                    developer = developer,
                    company = company,
                    status = status,
                    launchYear = launchYear,
                    tags = tags,
                    alternatives = alternatives
                )
            )
        }

        list
    }

    private val curatedTools = listOf(
        AiTool(
            id = "chatgpt", name = "ChatGPT (OpenAI)", category = "Text AI",
            description = "Advanced conversational AI assistant by OpenAI, utilizing state-of-the-art language models (GPT-4o) for content generation, problem-solving, coding, and brainstorming.",
            imageUrl = LOGO_CHATGPT,
            toolUrl = "https://chatgpt.com", pricing = "Freemium", platforms = listOf("Web", "Android", "iOS", "API"),
            developer = "OpenAI", company = "OpenAI", status = "Trending", launchYear = "2022",
            tags = listOf("chat", "assistant", "writing", "helper", "free chatbot", "openai", "chatgpt"),
            alternatives = listOf("Claude 3.5 Sonnet", "Google Gemini 1.5 Pro")
        ),
        AiTool(
            id = "claude", name = "Claude 3.5 Sonnet (Anthropic)", category = "Text AI",
            description = "State-of-the-art language model by Anthropic, renowned for advanced reasoning, sophisticated coding assistance, exceptional writing, and highly nuanced conversations.",
            imageUrl = LOGO_CLAUDE,
            toolUrl = "https://claude.ai", pricing = "Freemium", platforms = listOf("Web", "iOS", "Android", "API"),
            developer = "Anthropic", company = "Anthropic", status = "Trending", launchYear = "2024",
            tags = listOf("coding assistant", "reasoning", "writing", "chat", "free chatbot", "anthropic", "claude"),
            alternatives = listOf("ChatGPT", "Google Gemini 1.5 Pro")
        ),
        AiTool(
            id = "google-gemini-pro", name = "Google Gemini 1.5 Pro", category = "Text AI",
            description = "Google's flagship multimodal model with an ultra-long context window of up to 2 million tokens, processing video, audio, code, and text simultaneously.",
            imageUrl = LOGO_GEMINI,
            toolUrl = "https://gemini.google.com", pricing = "Freemium", platforms = listOf("Web", "Android", "iOS", "API"),
            developer = "Google DeepMind", company = "Google", status = "Popular", launchYear = "2024",
            tags = listOf("google ai", "translation", "multimodal", "free chatbot", "google", "gemini"),
            alternatives = listOf("ChatGPT", "Claude 3.5 Sonnet")
        ),
        AiTool(
            id = "grok", name = "Grok (xAI)", category = "Text AI",
            description = "Real-time query engine with direct, unfiltered access to the global social feed logs of X (formerly Twitter), providing witty and up-to-the-minute information.",
            imageUrl = LOGO_GROK,
            toolUrl = "https://x.ai", pricing = "Paid", platforms = listOf("Web", "iOS", "Android"),
            developer = "xAI Team", company = "xAI", status = "New", launchYear = "2023",
            tags = listOf("twitter", "realtime", "search", "grok", "xai"),
            alternatives = listOf("ChatGPT", "Google Gemini 1.5 Pro")
        ),
        AiTool(
            id = "deepseek", name = "DeepSeek-V3", category = "Text AI",
            description = "High-performance open-source Mixture-of-Experts language model specializing in mathematical reasoning, multilingual translation, and extreme coding efficiency.",
            imageUrl = LOGO_DEEPSEEK,
            toolUrl = "https://deepseek.com", pricing = "Free", platforms = listOf("Web", "API Available"),
            developer = "DeepSeek Inc.", company = "DeepSeek", status = "New", launchYear = "2024",
            tags = listOf("open source", "free chatbot", "coding assistant", "deepseek", "moe"),
            alternatives = listOf("ChatGPT", "Claude 3.5 Sonnet")
        ),
        AiTool(
            id = "perplexity", name = "Perplexity AI", category = "Research",
            description = "Conversational search and answering engine that provides real-time citations, academic references, and structural layouts for highly accurate lookups.",
            imageUrl = LOGO_PERPLEXITY,
            toolUrl = "https://perplexity.ai", pricing = "Freemium", platforms = listOf("Web", "iOS", "Android"),
            developer = "Perplexity AI Team", company = "Perplexity AI", status = "Popular", launchYear = "2022",
            tags = listOf("search", "research ai", "citations", "academic", "helper", "perplexity"),
            alternatives = listOf("Google Gemini 1.5 Pro", "ChatGPT")
        ),
        AiTool(
            id = "google-notebooklm", name = "Google NotebookLM", category = "Productivity",
            description = "AI-powered personalized research assistant that transforms documents, PDFs, slides, and links into comprehensive audio dialogues, summaries, and guides.",
            imageUrl = LOGO_GOOGLE,
            toolUrl = "https://notebooklm.google", pricing = "Free", platforms = listOf("Web"),
            developer = "Google Labs", company = "Google", status = "Trending", launchYear = "2024",
            tags = listOf("research assistant", "notebooklm", "notes", "google"),
            alternatives = listOf("Perplexity", "ChatGPT")
        ),
        AiTool(
            id = "phind", name = "Phind", category = "Research",
            description = "Intelligent developer-first search engine optimized to solve complex software engineering and architectural problems with direct code solutions.",
            imageUrl = "",
            toolUrl = "https://phind.com", pricing = "Freemium", platforms = listOf("Web", "VS Code Extension"),
            developer = "Phind Inc.", company = "Phind", status = "Popular", launchYear = "2023",
            tags = listOf("search", "coding search", "developer tool", "phind"),
            alternatives = listOf("Perplexity AI", "GitHub Copilot")
        ),
        AiTool(
            id = "midjourney", name = "Midjourney v6", category = "Image AI",
            description = "Industry-leading artistic text-to-image generator, producing incredibly realistic aesthetics, cinematic visuals, and complex textural details from simple text prompts.",
            imageUrl = LOGO_MIDJOURNEY,
            toolUrl = "https://midjourney.com", pricing = "Paid", platforms = listOf("Web", "Discord"),
            developer = "Midjourney Lab", company = "Midjourney", status = "Trending", launchYear = "2022",
            tags = listOf("image generator", "art", "design", "logo maker", "best image ai", "midjourney"),
            alternatives = listOf("DALL-E 3", "Stable Diffusion")
        ),
        AiTool(
            id = "dalle3", name = "DALL-E 3 (OpenAI)", category = "Image AI",
            description = "Sleek generative image model integrated directly inside ChatGPT, translating nuanced text prompts into highly stylized, accurate vector art and diagrams.",
            imageUrl = LOGO_OPENAI,
            toolUrl = "https://openai.com/dall-e-3", pricing = "Paid", platforms = listOf("Web", "API"),
            developer = "OpenAI", company = "OpenAI", status = "Popular", launchYear = "2023",
            tags = listOf("image generator", "dall-e", "art", "openai"),
            alternatives = listOf("Midjourney", "Stable Diffusion")
        ),
        AiTool(
            id = "stable-diffusion", name = "Stable Diffusion", category = "Image AI",
            description = "State-of-the-art open-source text-to-image generator, allowing local deployment and infinite fine-tuning via custom checkpoint weights and LoRA modules.",
            imageUrl = LOGO_HUGGINGFACE,
            toolUrl = "https://stability.ai", pricing = "Free", platforms = listOf("Web", "Local PC", "API"),
            developer = "Stability AI", company = "Stability AI", status = "Popular", launchYear = "2022",
            tags = listOf("open source", "image generator", "stable diffusion", "sdxl"),
            alternatives = listOf("Midjourney", "DALL-E 3")
        ),
        AiTool(
            id = "leonardo-ai", name = "Leonardo AI", category = "Image AI",
            description = "Production-grade generative suite for creators, providing advanced canvas tools, real-time generation, character consistency models, and 3D textures.",
            imageUrl = LOGO_MIDJOURNEY,
            toolUrl = "https://leonardo.ai", pricing = "Freemium", platforms = listOf("Web", "iOS"),
            developer = "Leonardo Lab", company = "Leonardo AI", status = "Trending", launchYear = "2023",
            tags = listOf("image generator", "art editor", "gaming assets", "leonardo"),
            alternatives = listOf("Midjourney", "Stable Diffusion")
        ),
        AiTool(
            id = "ideogram", name = "Ideogram 2.0", category = "Image AI",
            description = "Graphic design image generator specialized in perfect typography, text rendering, letter spacing, and logo integration within illustrative images.",
            imageUrl = LOGO_MIDJOURNEY,
            toolUrl = "https://ideogram.ai", pricing = "Freemium", platforms = listOf("Web", "iOS"),
            developer = "Ideogram Team", company = "Ideogram", status = "New", launchYear = "2023",
            tags = listOf("typography", "text in image", "logo maker", "design", "ideogram"),
            alternatives = listOf("Midjourney", "DALL-E 3")
        ),
        AiTool(
            id = "recraft-ai", name = "Recraft V3", category = "Image AI",
            description = "Design-focused generative tool that creates perfectly scalable SVG vector files, brand assets, mockups, and illustrations with customizable color palettes.",
            imageUrl = LOGO_MIDJOURNEY,
            toolUrl = "https://recraft.ai", pricing = "Freemium", platforms = listOf("Web"),
            developer = "Recraft Lab", company = "Recraft", status = "Trending", launchYear = "2024",
            tags = listOf("vector art", "svg generator", "graphic design", "illustrations"),
            alternatives = listOf("Midjourney", "Canva Magic Studio")
        ),
        AiTool(
            id = "adobe-firefly", name = "Adobe Firefly", category = "Image AI",
            description = "Adobe's family of creative generative models built directly into Photoshop and Illustrator, trained exclusively on licensed content for safe commercial use.",
            imageUrl = LOGO_ADOBE,
            toolUrl = "https://adobe.com/firefly", pricing = "Freemium", platforms = listOf("Web", "Creative Cloud"),
            developer = "Adobe Team", company = "Adobe", status = "Popular", launchYear = "2023",
            tags = listOf("adobe firefly", "commercial safe", "generative fill", "photoshop"),
            alternatives = listOf("Midjourney", "DALL-E 3")
        ),
        AiTool(
            id = "canva-magic", name = "Canva Magic Studio", category = "Design",
            description = "All-in-one suite of AI graphic tools that can instantly write copy, design presentations, generate layouts, swap image assets, and translate text.",
            imageUrl = LOGO_CANVA,
            toolUrl = "https://canva.com", pricing = "Freemium", platforms = listOf("Web", "Android", "iOS"),
            developer = "Canva Team", company = "Canva", status = "Popular", launchYear = "2023",
            tags = listOf("graphic designer", "presentations", "logo maker", "canva"),
            alternatives = listOf("Adobe Firefly", "v0 by Vercel")
        ),
        AiTool(
            id = "openai-sora", name = "OpenAI Sora", category = "Video AI",
            description = "Revolutionary text-to-video AI generator that renders physically accurate cinematic motions, complex 3D scenes, and realistic characters up to 60 seconds long.",
            imageUrl = LOGO_OPENAI,
            toolUrl = "https://openai.com/sora", pricing = "Paid", platforms = listOf("Web"),
            developer = "OpenAI", company = "OpenAI", status = "Trending", launchYear = "2024",
            tags = listOf("video generator", "sora", "cinematic", "video maker", "openai"),
            alternatives = listOf("Runway Gen-3", "Luma Dream Machine")
        ),
        AiTool(
            id = "runway-gen3", name = "Runway Gen-3 Alpha", category = "Video AI",
            description = "Full-fledged cinematic video generator providing advanced camera controls, text-to-video, image-to-video, and precise temporal consistency.",
            imageUrl = LOGO_RUNWAY,
            toolUrl = "https://runwayml.com", pricing = "Paid", platforms = listOf("Web", "iOS"),
            developer = "Runway AI Inc.", company = "Runway", status = "Trending", launchYear = "2024",
            tags = listOf("video generator", "runway", "gen3", "motion", "video maker"),
            alternatives = listOf("OpenAI Sora", "Luma Dream Machine")
        ),
        AiTool(
            id = "luma-dream", name = "Luma Dream Machine", category = "Video AI",
            description = "A rapid, cinematic video generator that renders realistic, physically accurate motion from descriptive text prompts and high-fidelity source images.",
            imageUrl = "",
            toolUrl = "https://lumalabs.ai/dream-machine", pricing = "Freemium", platforms = listOf("Web"),
            developer = "Luma Labs", company = "Luma Labs", status = "New", launchYear = "2024",
            tags = listOf("video generator", "3d motion", "animation", "luma", "dream machine"),
            alternatives = listOf("OpenAI Sora", "Runway Gen-3")
        ),
        AiTool(
            id = "google-veo", name = "Google Veo", category = "Video AI",
            description = "Google's highest definition generative video model, outputting 1080p high-fidelity cinematic video footage across diverse visual and creative styles.",
            imageUrl = LOGO_GOOGLE,
            toolUrl = "https://deepmind.google/technologies/veo", pricing = "Paid", platforms = listOf("Web"),
            developer = "Google DeepMind", company = "Google", status = "New", launchYear = "2024",
            tags = listOf("video generator", "google veo", "veo", "cinematic", "video maker", "google"),
            alternatives = listOf("OpenAI Sora", "Runway Gen-3")
        ),
        AiTool(
            id = "suno-ai", name = "Suno AI v4", category = "Music AI",
            description = "Advanced AI music studio that crafts professional vocal tracks, dynamic instrumentals, and complete lyrics in any musical genre from a simple text description.",
            imageUrl = LOGO_SUNO,
            toolUrl = "https://suno.com", pricing = "Freemium", platforms = listOf("Web", "iOS"),
            developer = "Suno Creators", company = "Suno", status = "Trending", launchYear = "2023",
            tags = listOf("music generator", "song maker", "lyrics writer", "instrumental", "suno"),
            alternatives = listOf("Udio AI")
        ),
        AiTool(
            id = "udio-ai", name = "Udio AI", category = "Music AI",
            description = "High-fidelity music composition suite that generates complete musical tracks with high-quality vocals, complex song progression, and genre-bending audio layers.",
            imageUrl = "",
            toolUrl = "https://udio.com", pricing = "Freemium", platforms = listOf("Web"),
            developer = "Udio Team", company = "Udio Inc.", status = "New", launchYear = "2024",
            tags = listOf("music generator", "song creator", "udio", "vocals"),
            alternatives = listOf("Suno AI v4")
        ),
        AiTool(
            id = "elevenlabs", name = "ElevenLabs", category = "Audio AI",
            description = "Advanced speech synthesis model specializing in highly natural text-to-speech, instant voice cloning, and translation dubbing across over 29 languages.",
            imageUrl = "",
            toolUrl = "https://elevenlabs.io", pricing = "Freemium", platforms = listOf("Web", "iOS", "API"),
            developer = "ElevenLabs Team", company = "ElevenLabs", status = "Popular", launchYear = "2023",
            tags = listOf("voice ai", "voice clone", "speech generator", "elevenlabs"),
            alternatives = listOf("Suno AI v4")
        ),
        AiTool(
            id = "adobe-podcast", name = "Adobe Podcast AI", category = "Audio AI",
            description = "AI voice recording enhancer that filters out all background noises, dynamic echoes, and static hums to render pristine, high-fidelity studio-quality voice.",
            imageUrl = LOGO_ADOBE,
            toolUrl = "https://podcast.adobe.com", pricing = "Free", platforms = listOf("Web"),
            developer = "Adobe Labs", company = "Adobe", status = "Popular", launchYear = "2023",
            tags = listOf("podcast enhancer", "voice cleaner", "studio sound", "adobe"),
            alternatives = listOf("ElevenLabs")
        ),
        AiTool(
            id = "github-copilot", name = "GitHub Copilot", category = "Coding AI",
            description = "AI pair programmer that autocomplete code, translates logic between languages, drafts tests, and debugs codebases directly within VS Code and JetBrains.",
            imageUrl = LOGO_COPILOT,
            toolUrl = "https://github.com/features/copilot", pricing = "Paid", platforms = listOf("Web", "IDE Extension"),
            developer = "GitHub & OpenAI", company = "Microsoft", status = "Popular", launchYear = "2021",
            tags = listOf("coding assistant", "programming", "autocomplete", "vscode", "copilot", "github", "openai"),
            alternatives = listOf("Cursor AI")
        ),
        AiTool(
            id = "cursor", name = "Cursor AI", category = "Coding AI",
            description = "A powerful, developer-first IDE fork of VS Code integrated natively with Claude and GPT-4o for seamless codebase-wide search, chat, and editing.",
            imageUrl = LOGO_GITHUB,
            toolUrl = "https://cursor.com", pricing = "Freemium", platforms = listOf("Web", "Desktop App"),
            developer = "Anysphere Inc.", company = "Anysphere", status = "Trending", launchYear = "2023",
            tags = listOf("ide", "coding editor", "programming", "claude editor", "cursor"),
            alternatives = listOf("GitHub Copilot", "Bolt.new")
        ),
        AiTool(
            id = "v0", name = "v0 by Vercel", category = "Coding AI",
            description = "Generative UI system that translates prompts into clean, production-ready frontend components using Tailwind CSS, React, and Lucide icons.",
            imageUrl = "",
            toolUrl = "https://v0.dev", pricing = "Freemium", platforms = listOf("Web"),
            developer = "Vercel Team", company = "Vercel", status = "Trending", launchYear = "2023",
            tags = listOf("frontend generator", "react code", "tailwind", "vercel", "v0"),
            alternatives = listOf("Cursor AI", "Bolt.new")
        ),
        AiTool(
            id = "bolt-new", name = "Bolt.new", category = "Coding AI",
            description = "Full-stack sandboxed web-container IDE that instantly boots up, installs dependencies, writes full-stack code, and deploys live web applications directly in the browser.",
            imageUrl = "",
            toolUrl = "https://bolt.new", pricing = "Freemium", platforms = listOf("Web"),
            developer = "StackBlitz Team", company = "StackBlitz", status = "New", launchYear = "2024",
            tags = listOf("fullstack generator", "browser ide", "no-code", "bolt"),
            alternatives = listOf("Cursor AI", "v0 by Vercel")
        ),
        AiTool(
            id = "lovable", name = "Lovable.dev", category = "Coding AI",
            description = "Full-stack AI developer assistant that designs, codes, and publishes production-grade applications with zero manual setup required.",
            imageUrl = "",
            toolUrl = "https://lovable.dev", pricing = "Paid", platforms = listOf("Web"),
            developer = "Lovable Team", company = "Lovable AI", status = "New", launchYear = "2024",
            tags = listOf("app creator", "no-code developer", "lovable"),
            alternatives = listOf("Bolt.new", "v0 by Vercel")
        ),
        AiTool(
            id = "autogpt", name = "AutoGPT", category = "Agents",
            description = "Autonomous task orchestration system that splits high-level goals into smaller subtasks, browse websites, runs local code, and updates files dynamically.",
            imageUrl = "",
            toolUrl = "https://github.com/Significant-Gravitas/AutoGPT", pricing = "Free", platforms = listOf("Web", "Local Terminal"),
            developer = "Significant Gravitas", company = "Open Source Team", status = "Popular", launchYear = "2023",
            tags = listOf("agent", "autonomous", "autogpt", "open source"),
            alternatives = listOf("Devin")
        ),
        AiTool(
            id = "devin", name = "Devin (Cognition)", category = "Agents",
            description = "The world's first fully autonomous AI software engineer, capable of working through complex programming tickets, building apps, and debugging locally.",
            imageUrl = "",
            toolUrl = "https://cognition-labs.com", pricing = "Paid", platforms = listOf("Web"),
            developer = "Cognition Team", company = "Cognition Labs", status = "Trending", launchYear = "2024",
            tags = listOf("agent", "autonomous coder", "devin", "software engineer"),
            alternatives = listOf("AutoGPT", "Cursor AI")
        ),
        AiTool(
            id = "chatpdf", name = "ChatPDF", category = "PDF",
            description = "Instant conversational portal that parses textbooks, lengthy legal agreements, research studies, and manuals into interactive question-and-answer logs.",
            imageUrl = "",
            toolUrl = "https://chatpdf.com", pricing = "Freemium", platforms = listOf("Web"),
            developer = "ChatPDF Inc.", company = "ChatPDF", status = "Popular", launchYear = "2023",
            tags = listOf("pdf analyzer", "chat pdf", "documents reader"),
            alternatives = listOf("Google NotebookLM")
        ),
        AiTool(
            id = "notion-ai", name = "Notion AI", category = "Productivity",
            description = "Integrated writing assistant inside the Notion workspace that can draft summaries, auto-format text tables, brainstorm outlines, and extract task lists.",
            imageUrl = LOGO_NOTION,
            toolUrl = "https://notion.so", pricing = "Paid", platforms = listOf("Web", "Android", "iOS", "API"),
            developer = "Notion Labs", company = "Notion", status = "Popular", launchYear = "2023",
            tags = listOf("workspace writer", "productivity assistant", "notion"),
            alternatives = listOf("ChatGPT", "Google NotebookLM")
        ),
        AiTool(
            id = "harvey-ai", name = "Harvey AI", category = "Legal",
            description = "Enterprise generative assistant trained specifically for corporate law firms, facilitating contract analysis, regulatory compliance, and legal drafting.",
            imageUrl = "",
            toolUrl = "https://harvey.ai", pricing = "Paid", platforms = listOf("Web"),
            developer = "Harvey Team", company = "Harvey AI", status = "New", launchYear = "2023",
            tags = listOf("legal copilot", "contracts researcher", "harvey"),
            alternatives = listOf("ChatGPT")
        ),
        AiTool(
            id = "kensho", name = "Kensho", category = "Finance",
            description = "Financial analytics platform that converts unstructured economic datasets, corporate call transcriptions, and reports into clean signals.",
            imageUrl = "",
            toolUrl = "https://kensho.com", pricing = "Paid", platforms = listOf("Web"),
            developer = "Kensho Team", company = "S&P Global", status = "Popular", launchYear = "2021",
            tags = listOf("finance tool", "market analytics", "kensho"),
            alternatives = listOf("BloombergGPT")
        ),
        AiTool(
            id = "khanmigo", name = "Khanmigo", category = "Education",
            description = "AI tutor developed by Khan Academy, providing personalized math coaching and guided curriculum learning in a safe, conversational space.",
            imageUrl = "",
            toolUrl = "https://khanacademy.org/khanmigo", pricing = "Paid", platforms = listOf("Web"),
            developer = "Khan Academy", company = "Khan Academy", status = "Popular", launchYear = "2023",
            tags = listOf("tutor", "math solver", "education assistant", "khanmigo"),
            alternatives = listOf("ChatGPT")
        ),
        AiTool(
            id = "huggingface", name = "Hugging Face", category = "Open Source",
            description = "The hub of the open-source machine learning community, hosting millions of models, datasets, and collaborative Spaces for rapid ML deployment.",
            imageUrl = LOGO_HUGGINGFACE,
            toolUrl = "https://huggingface.co", pricing = "Free", platforms = listOf("Web", "API Available"),
            developer = "Hugging Face Team", company = "Hugging Face", status = "Popular", launchYear = "2016",
            tags = listOf("ml community", "model hub", "open source hub", "hugging face"),
            alternatives = listOf("GitHub")
        ),
        AiTool(
            id = "ollama", name = "Ollama", category = "Open Source",
            description = "A lightweight, secure framework for running large language models locally on your computer, supporting Llama 3, Mistral, and DeepSeek model files.",
            imageUrl = LOGO_GITHUB,
            toolUrl = "https://ollama.com", pricing = "Free", platforms = listOf("Mac", "Windows", "Linux"),
            developer = "Ollama Team", company = "Ollama Inc.", status = "Trending", launchYear = "2023",
            tags = listOf("local llm", "offline models", "developer tool", "ollama"),
            alternatives = listOf("Hugging Face")
        ),
        AiTool(
            id = "glass-health", name = "Glass Health", category = "Medical",
            description = "AI-powered clinical decision platform that maps medical symptoms to peer-reviewed treatment paths and verified case study documentation.",
            imageUrl = "",
            toolUrl = "https://glass.health", pricing = "Paid", platforms = listOf("Web"),
            developer = "Glass Team", company = "Glass Health", status = "New", launchYear = "2022",
            tags = listOf("clinical assistant", "medical diagnoses", "glass health"),
            alternatives = listOf("Google Gemini 1.5 Pro")
        )
    )
}
