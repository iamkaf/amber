import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'Amber',
  description: 'A comprehensive multiloader library for Minecraft mod development',
  lang: 'en-US',
  
  // Set base path for GitHub Pages deployment
  base: '/amber/',
  
  // Set favicon
  head: [
    ['link', { rel: 'icon', href: '/icon.png' }]
  ],
  
  themeConfig: {
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Guide', link: '/guide/getting-started' },
      { text: 'API', link: '/api/core' }
    ],

    sidebar: [
      {
        text: 'Introduction',
        items: [
          { text: 'What is Amber?', link: '/' },
          { text: 'Getting Started', link: '/guide/getting-started' },
          { text: 'Installation', link: '/guide/installation' }
        ]
      },
      {
        text: 'Core API',
        items: [
          { text: 'Core Module', link: '/api/core' },
          { text: 'Platform Abstraction', link: '/api/platform' }
        ]
      },
      {
        text: 'Systems',
        items: [
          { text: 'Registry System', link: '/systems/registry' },
          { text: 'Event System', link: '/systems/events' },
          { text: 'Networking', link: '/systems/networking' },
          { text: 'Configuration', link: '/systems/configuration' },
          { text: 'Commands', link: '/systems/commands' }
        ]
      },
      {
        text: 'Utilities',
        items: [
          { text: 'Item Helpers', link: '/utilities/items' },
          { text: 'Inventory Helpers', link: '/utilities/inventory' },
          { text: 'Player Helpers', link: '/utilities/player' },
          { text: 'Level Helpers', link: '/utilities/level' },
          { text: 'Sound Helpers', link: '/utilities/sound' },
          { text: 'Math Utilities', link: '/utilities/math' }
        ]
      },
      {
        text: 'Advanced',
        items: [
          { text: 'Cross-Platform Development', link: '/advanced/cross-platform' },
          { text: 'Best Practices', link: '/advanced/best-practices' },
          { text: 'Migration Guide', link: '/advanced/migration' }
        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/iamkaf/amber' }
    ],

    search: {
      provider: 'local'
    },

    editLink: {
      pattern: 'https://github.com/iamkaf/amber/edit/main/docs/:path',
      text: 'Edit this page on GitHub'
    },

    footer: {
      message: 'Released under the MIT License.',
      copyright: 'Copyright © 2024 iamkaf'
    }
  },

  markdown: {
    // Configure syntax highlighting for Gradle
    // Map gradle to groovy for syntax highlighting since Gradle uses Groovy DSL
    languageAlias: {
      gradle: 'groovy'
    },
    theme: {
      light: 'github-light',
      dark: 'github-dark'
    }
  },

  // Ignore dead links during build
  ignoreDeadLinks: true,

  vite: {
    define: {
      __VUE_OPTIONS_API__: false
    },
    server: {
      fs: {
        // Allow serving files from one level up
        allow: ['..']
      }
    },
    optimizeDeps: {
      exclude: ['vitepress']
    }
  }
})