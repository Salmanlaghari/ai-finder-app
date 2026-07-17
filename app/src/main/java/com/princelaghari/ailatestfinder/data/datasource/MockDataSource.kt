package com.princelaghari.ailatestfinder.data.datasource

import com.princelaghari.ailatestfinder.domain.model.AiTool

object MockDataSource {
    val aiTools = listOf(
        // Text AI
        AiTool(
            id = "txt_1",
            name = "ChatGPT",
            category = "Text AI",
            description = "OpenAI's state-of-the-art conversational AI model for text generation, translation, and general assistance.",
            imageUrl = "https://images.unsplash.com/photo-1677442136019-21780efad99a?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://chatgpt.com"
        ),
        AiTool(
            id = "txt_2",
            name = "Claude 3.5 Sonnet",
            category = "Text AI",
            description = "Anthropic's highly intelligent, safe AI model with advanced reasoning, coding, and long-context capabilities.",
            imageUrl = "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://claude.ai"
        ),
        AiTool(
            id = "txt_3",
            name = "Jasper AI",
            category = "Text AI",
            description = "An enterprise-grade AI copywriting assistant specialized in SEO-optimized blogs, marketing copy, and emails.",
            imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://jasper.ai"
        ),

        // Image AI
        AiTool(
            id = "img_1",
            name = "Midjourney v6",
            category = "Image AI",
            description = "An independent research lab exploring new mediums of thought. Renowned for hyper-realistic and artistic image generation.",
            imageUrl = "https://images.unsplash.com/photo-1614741118887-7a4ee193a5fa?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://midjourney.com"
        ),
        AiTool(
            id = "img_2",
            name = "Stable Diffusion",
            category = "Image AI",
            description = "Stability AI's open-source latent text-to-image diffusion model producing extremely high-quality custom visuals.",
            imageUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://stability.ai"
        ),
        AiTool(
            id = "img_3",
            name = "DALL-E 3",
            category = "Image AI",
            description = "OpenAI's latest image model, offering rich, highly detailed image synthesis matching nuanced text descriptions.",
            imageUrl = "https://images.unsplash.com/photo-1544377193-33dcf4d68fb5?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://openai.com/dall-e-3"
        ),

        // Video AI
        AiTool(
            id = "vid_1",
            name = "Runway Gen-3",
            category = "Video AI",
            description = "A powerful AI video generation tool offering text-to-video and image-to-video transformation with realistic physics.",
            imageUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://runwayml.com"
        ),
        AiTool(
            id = "vid_2",
            name = "OpenAI Sora",
            category = "Video AI",
            description = "An AI model that can create realistic and imaginative scenes from text instructions, supporting up to 60 seconds duration.",
            imageUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://openai.com/sora"
        ),
        AiTool(
            id = "vid_3",
            name = "Pika Labs",
            category = "Video AI",
            description = "An amazing idea-to-video platform that makes it super easy for anyone to animate their imagination with professional quality.",
            imageUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://pika.art"
        ),

        // Coding AI
        AiTool(
            id = "code_1",
            name = "Cursor AI",
            category = "Coding AI",
            description = "An AI-powered code editor built on top of VS Code, enabling rapid software engineering with contextual codebase chats.",
            imageUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://cursor.sh"
        ),
        AiTool(
            id = "code_2",
            name = "GitHub Copilot",
            category = "Coding AI",
            description = "Your AI pair programmer, suggesting complete lines or entire functions directly inside your IDE environment.",
            imageUrl = "https://images.unsplash.com/photo-1542831371-29b0f74f9713?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://github.com/features/copilot"
        ),
        AiTool(
            id = "code_3",
            name = "Tabnine",
            category = "Coding AI",
            description = "A private, secure AI coding assistant that autocomplete code, protects privacy, and strictly adheres to your guidelines.",
            imageUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://tabnine.com"
        ),

        // Audio Tools
        AiTool(
            id = "aud_1",
            name = "ElevenLabs",
            category = "Audio Tools",
            description = "A high-fidelity speech synthesis tool featuring exceptionally realistic voice generation, voice cloning, and text-to-speech.",
            imageUrl = "https://images.unsplash.com/photo-1484755560695-a4cfde12d207?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://elevenlabs.io"
        ),
        AiTool(
            id = "aud_2",
            name = "Suno AI",
            category = "Audio Tools",
            description = "An AI music generator that creates full songs including lyrics, vocals, and arrangements from simple text prompts.",
            imageUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://suno.com"
        ),
        AiTool(
            id = "aud_3",
            name = "Adobe Podcast AI",
            category = "Audio Tools",
            description = "AI-powered audio recording and editing web tool that makes spoken voice sound as if it was recorded in a professional studio.",
            imageUrl = "https://images.unsplash.com/photo-1478737270239-2f02b77fc618?auto=format&fit=crop&w=300&q=80",
            toolUrl = "https://podcast.adobe.com"
        )
    )
}
