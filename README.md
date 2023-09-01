[![Curse Forge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/valkyrie)
[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/valkyrie)

[![Buy Me a Coffee](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/buymeacoffee-singular_vector.svg)](https://www.buymeacoffee.com/desoroxxx)
[![Discord](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg)](https://discord.gg/hKpUYx7VwS)

[![Java 8](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/built-with/java8_vector.svg)](https://adoptium.net/temurin/releases/?version=8)
[![Gradle](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/built-with/gradle_vector.svg)](https://gradle.org/)
[![Forge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg)](http://files.minecraftforge.net/maven/net/minecraftforge/forge/index_1.12.2.html)

# Valkyrie

As the Valkyries' whispers guide us, we carve our path to a Valhalla of optimized Minecraft gameplay.

Valkyrie is a mod designed to optimize client-side operations while simultaneously introducing some valuable features.

Though currently in its beta stage, rest assured that Valkyrie remains fully stable. The 'beta' label simply reflects our ongoing commitment to continual improvement and optimization, we're not done enhancing its capabilities just yet!

We greatly value your feedback and ideas. If you notice anything missing or have specific optimizations in mind that you'd like us to incorporate, don't hesitate to reach out. You can share your suggestions or report issues on our GitHub page or join the conversation on our Discord server.

## Performance Enhancements

Valkyrie at its core is designed to optimize the client side to make your FPS higher and more stable, here is the list of things it optimizes:

- Faster cloud rendering
- Faster `ViewFrustum` was entirely optimized improving visual terrain loading speed and gives more stable FPS
- Faster `MathHelper` which improve the speed of mathematical operations
- Faster `RenderGlobal` which improves rendering speed
- Faster `WorldVertexBufferUploader` which improves rendering speed
- Slightly faster `ModelRenderer#render` which improve rendering speed

<details>
<summary>Technical Details</summary>

- `ViewFrustum` was optimized by reducing in loop calculations, doing less work, using bitwise operations, and reducing nested loops
- `MathHelper` was optimized by using [Jafama](https://github.com/jeffhain/jafama)
- `RenderGlobal#setupTerrain` was optimized removing unnecessary duplication and merging of the chunk to update queue and optimizing the iteration process
- `RenderGlobal#getRenderChunkOffset` was optimized with bitwise operations which improve its speed, which helps with making `RenderGlobal#setupTerrain` faster
- `WorldVertexBufferUploader#draw` was optimized by keeping track of the index of the current element in the post-render loop making it O(n) instead of O(n^2)
- `ModelRenderer#render` was optimized by using a rotation matrix thus reducing OpenGL calls, which slightly improve performance on complex models

</details>

## Features

- **Configurable Zoom:** Zoom in with precision or pan out for a broader view with Valkyrie's flexible and user-friendly zoom functionality.
- **Bigger Atlas:** Valkyrie allows you to have a texture atlas as big as your GPU really supports.
- **Colored Clouds:** Experience the ethereal beauty of clouds tinted by the rising and setting sun with Valkyrie's Colored Clouds feature.
- **Independent Clouds Render Distance:** Gain the power to customize your clouds' render distance independently, enabling them to extend beyond the terrain render distance.
- **Modern Icons & Logo:** By default Valkyrie changes the main menu logo to the newer one as well as the window icon
- **Window Customization:** Personalize your Minecraft window title and icon with Valkyrie, a handy feature for modpack developers.
- **Bug Fixes:** Beyond enhancing performance and aesthetics, Valkyrie also addresses Minecraft bugs such as [MC-67532](https://bugs.mojang.com/browse/MC-67532).
- **Old Java Detection:** Valkyrie will scan which Java version are you using and warn you if it's outdated as well as if you are using 32 Bit Java.

## FAQ

- Is this compatible with OptiFine?
  - Yes, although keep in mind that some Valkyrie features will be disabled when paired with OptiFine
- Will you add `X`?
  - Whether it is a feature that you miss from Optifine or just something that you would like, be sure to tell me.

---

[![BisectHostingPromoBanner](https://www.bisecthosting.com/partners/custom-banners/d410513a-9aee-467a-96eb-88eb0976af9d.webp)](https://bisecthosting.com/Desoroxxx?r=Valkyrie+GitHub)

## Want to have your own mod or support me?

If you're looking for a mod but don't have the development skills or time, consider commissioning me!
My commissions are currently open and I would be happy to create a custom mod to fit your needs as long as you provide assets.

[Commissions]

You can also support me on a monthly basis by becoming a member.
To thank you will have the possibility to access exlcusive post and messages, Discord channel for WIP content, and even access to unreleased Prototypes or WIP Projects.

[Membership]

You can also [buy me a hot chocolate].

[Commissions]: https://www.buymeacoffee.com/desoroxxx/commissions
[Membership]: https://www.buymeacoffee.com/desoroxxx/membership
[buy me a hot chocolate]: https://www.buymeacoffee.com/desoroxxx
