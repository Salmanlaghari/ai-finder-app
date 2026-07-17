package com.princelaghari.ailatestfinder.data.datasource

import com.princelaghari.ailatestfinder.domain.model.AiTool

object MockDataSource {
    val aiTools = listOf(
        // Text AI
        AiTool(
            id = "txt_0",
            name = "GPT-4o",
            category = "Text AI",
            description = "OpenAI's flagship multimodal model, combining text, vision, and audio capabilities in real-time with extreme speed.",
            imageUrl = "https://images.unsplash.com/photo-1677442136019-21780efad99a?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://openai.com/gpt-4o"
        ),
        AiTool(
            id = "txt_1",
            name = "Claude 3.5 Sonnet",
            category = "Text AI",
            description = "Anthropic's state-of-the-art conversational AI model. Sets industry benchmarks for graduate-level reasoning and programming.",
            imageUrl = "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://claude.ai"
        ),
        AiTool(
            id = "txt_2",
            name = "ChatGPT",
            category = "Text AI",
            description = "The pioneer conversational AI by OpenAI, supporting daily workflows, deep research, coding, and interactive learning.",
            imageUrl = "https://images.unsplash.com/photo-1677442135402-4f0a0d4a5b82?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://chatgpt.com"
        ),
        AiTool(
            id = "txt_3",
            name = "Jasper AI",
            category = "Text AI",
            description = "Professional copywriter specialized in marketing, blog posts, high-conversion ad text, and enterprise content generation.",
            imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://jasper.ai"
        ),

        // Image AI
        AiTool(
            id = "img_1",
            name = "Midjourney v6",
            category = "Image AI",
            description = "The absolute leader in hyper-realistic and high-fidelity artistic image generation, rendering rich details from prompts.",
            imageUrl = "https://images.unsplash.com/photo-1614741118887-7a4ee193a5fa?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://midjourney.com"
        ),
        AiTool(
            id = "img_2",
            name = "Stable Diffusion 3",
            category = "Image AI",
            description = "Stability AI's open weights model for realistic image synthesis, text rendering inside images, and custom prompt adherence.",
            imageUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://stability.ai"
        ),
        AiTool(
            id = "img_3",
            name = "DALL-E 3",
            category = "Image AI",
            description = "OpenAI's precise text-to-image generator integrated inside ChatGPT, translating nuanced concepts into beautiful artwork.",
            imageUrl = "https://images.unsplash.com/photo-1544377193-33dcf4d68fb5?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://openai.com/dall-e-3"
        ),

        // Video AI
        AiTool(
            id = "vid_0",
            name = "Google Veo",
            category = "Video AI",
            description = "Google's most capable video generation model. Creates high-quality 1080p cinematic videos from text, image, and video prompts.",
            imageUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://deepmind.google/technologies/veo"
        ),
        AiTool(
            id = "vid_1",
            name = "OpenAI Sora",
            category = "Video AI",
            description = "An AI model that can create highly detailed, realistic, and imaginative scenes from natural text instructions with physics-aware continuity.",
            imageUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://openai.com/sora"
        ),
        AiTool(
            id = "vid_2",
            name = "Luma Dream Machine",
            category = "Video AI",
            description = "A rapid, cinematic video generator that renders realistic, physically accurate motion from simple text descriptions and pictures.",
            imageUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://lumalabs.ai/dream-machine"
        ),
        AiTool(
            id = "vid_3",
            name = "Runway Gen-3 Alpha",
            category = "Video AI",
            description = "A major step forward for high-fidelity, high-control text-to-video and image-to-video generation by Runway ML.",
            imageUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://runwayml.com"
        ),

        // Coding AI
        AiTool(
            id = "code_1",
            name = "Cursor AI",
            category = "Coding AI",
            description = "A powerful, native VS Code fork integrated with conversational LLMs to chat, autocomplete, and generate code directly in context.",
            imageUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://cursor.sh"
        ),
        AiTool(
            id = "code_2",
            name = "GitHub Copilot",
            category = "Coding AI",
            description = "The industry standard AI pair programmer, suggesting precise code contextually inside standard development IDEs.",
            imageUrl = "https://images.unsplash.com/photo-1542831371-29b0f74f9713?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://github.com/features/copilot"
        ),
        AiTool(
            id = "code_3",
            name = "Tabnine",
            category = "Coding AI",
            description = "AI code completion companion specialized in enterprise security, private models, and safe private hosting.",
            imageUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://tabnine.com"
        ),

        // Audio Tools
        AiTool(
            id = "aud_1",
            name = "Suno AI",
            category = "Audio Tools",
            description = "An ultra-premium AI music studio that crafts professional vocal tracks, lyrics, and instrumentals from descriptive prompts.",
            imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://suno.com"
        ),
        AiTool(
            id = "aud_2",
            name = "ElevenLabs",
            category = "Audio Tools",
            description = "The most realistic AI voice generator and text-to-speech engine, supporting voice cloning, sound effects, and multilingual speech.",
            imageUrl = "https://images.unsplash.com/photo-1484755560695-a4cfde12d207?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://elevenlabs.io"
        ),
        AiTool(
            id = "aud_3",
            name = "Adobe Podcast AI",
            category = "Audio Tools",
            description = "AI-powered web suite that enhances audio recordings to sound as if they were captured in a soundproof professional studio.",
            imageUrl = "https://images.unsplash.com/photo-1478737270239-2f02b77fc618?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://podcast.adobe.com"
        )
    )
}
