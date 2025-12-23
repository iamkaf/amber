import { defineConfig } from 'vitepress'
import { v8Sidebar } from './sidebars/v8'
import { v9Sidebar } from './sidebars/v9'

export default defineConfig({
  title: 'Amber',
  description: 'A comprehensive multiloader library for Minecraft mod development',
  lang: 'en-US',

  // Set base path for GitHub Pages deployment
  base: '/amber/',

  // Set favicon
  head: [
    ['link', { rel: 'icon', href: '/amber/favicon.ico' }],
    ['link', { rel: 'icon', type: 'image/png', sizes: '16x16', href: '/amber/favicon-16x16.png' }],
    ['link', { rel: 'icon', type: 'image/png', sizes: '32x32', href: '/amber/favicon-32x32.png' }],
    ['link', { rel: 'apple-touch-icon', sizes: '180x180', href: '/amber/apple-touch-icon.png' }]
  ],

  themeConfig: {
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Guide', link: '/v9/guide/getting-started' },
      { text: 'API', link: '/v9/api/core' },
      {
        text: 'Versions',
        items: [
          { text: 'v9 (Minecraft 1.21.11)', link: '/v9/' },
          { text: 'v8 (Minecraft 1.21.10)', link: '/v8/' }
        ]
      }
    ],

    // Merge all versioned sidebars - VitePress picks the correct one based on route
    sidebar: {
      ...v9Sidebar,
      ...v8Sidebar
    },

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
      copyright: 'Copyright © 2025 iamkaf'
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

  srcExclude: ['_internal/**/*.md'],
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
