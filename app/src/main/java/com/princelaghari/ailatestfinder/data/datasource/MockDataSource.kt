package com.princelaghari.ailatestfinder.data.datasource

import com.princelaghari.ailatestfinder.domain.model.AiTool

object MockDataSource {
    val aiTools = listOf(
        // Text AI
        AiTool(
            id = "txt_1",
            name = "ChatGPT",
            category = "Text AI",
            description = "Advanced conversational AI by OpenAI.",
            imageUrl = "https://images.unsplash.com/photo-1677442136019-21780efad99a?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://chatgpt.com"
        ),
        AiTool(
            id = "txt_2",
            name = "Claude 3.5 Sonnet",
            category = "Text AI",
            description = "State-of-the-art reasoning model by Anthropic.",
            imageUrl = "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://claude.ai"
        ),
        AiTool(
            id = "txt_3",
            name = "GPT-4o",
            category = "Text AI",
            description = "OpenAI's flagship multimodal model, combining text, vision, and audio capabilities in real-time.",
            imageUrl = "https://images.unsplash.com/photo-1677442135402-4f0a0d4a5b82?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://openai.com/gpt-4o"
        ),

        // Image AI
        AiTool(
            id = "img_1",
            name = "Midjourney",
            category = "Image AI",
            description = "Ultra-realistic text-to-image generator.",
            imageUrl = "https://images.unsplash.com/photo-1614741118887-7a4ee193a5fa?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://midjourney.com"
        ),
        AiTool(
            id = "img_2",
            name = "Stable Diffusion 3",
            category = "Image AI",
            description = "Stability AI's open weights model for realistic image synthesis and prompt adherence.",
            imageUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://stability.ai"
        ),

        // Video AI
        AiTool(
            id = "vid_1",
            name = "Google Veo",
            category = "Video AI",
            description = "High-definition generative video model.",
            imageUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://deepmind.google/technologies/veo"
        ),
        AiTool(
            id = "vid_2",
            name = "OpenAI Sora",
            category = "Video AI",
            description = "Text-to-video generation model.",
            imageUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://openai.com/sora"
        ),
        AiTool(
            id = "vid_3",
            name = "Luma Dream Machine",
            category = "Video AI",
            description = "A rapid, cinematic video generator that renders realistic, physically accurate motion.",
            imageUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://lumalabs.ai/dream-machine"
        ),

        // Coding AI
        AiTool(
            id = "code_1",
            name = "GitHub Copilot",
            category = "Coding AI",
            description = "AI pair programmer.",
            imageUrl = "https://images.unsplash.com/photo-1542831371-29b0f74f9713?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://github.com/features/copilot"
        ),
        AiTool(
            id = "code_2",
            name = "Cursor AI",
            category = "Coding AI",
            description = "A powerful, native VS Code fork integrated with conversational LLMs to generate code in context.",
            imageUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://cursor.sh"
        ),

        // Audio Tools
        AiTool(
            id = "aud_1",
            name = "Suno AI",
            category = "Audio Tools",
            description = "An ultra-premium AI music studio that crafts professional vocal tracks, lyrics, and instrumentals.",
            imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://suno.com"
        ),
        AiTool(
            id = "aud_2",
            name = "ElevenLabs",
            category = "Audio Tools",
            description = "The most realistic AI voice generator and text-to-speech engine supporting voice cloning.",
            imageUrl = "https://images.unsplash.com/photo-1484755560695-a4cfde12d207?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://elevenlabs.io"
        )
    )
}
