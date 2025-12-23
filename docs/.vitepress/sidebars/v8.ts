import type { DefaultTheme } from 'vitepress'

export const v8Sidebar: DefaultTheme.Sidebar = {
  '/v8/': [
    {
      text: 'Introduction',
      items: [
        { text: 'What is Amber?', link: '/v8/' },
        { text: 'Getting Started', link: '/v8/guide/getting-started' },
        { text: 'Installation', link: '/v8/guide/installation' }
      ]
    },
    {
      text: 'Core API',
      items: [
        { text: 'Core Module', link: '/v8/api/core' },
        { text: 'Platform Abstraction', link: '/v8/api/platform' }
      ]
    },
    {
      text: 'Systems',
      items: [
        { text: 'Registry System', link: '/v8/systems/registry' },
        { text: 'Event System', link: '/v8/systems/events' },
        { text: 'Creative Tabs', link: '/v8/systems/creative-tabs' },
        { text: 'Networking', link: '/v8/systems/networking' },
        { text: 'Configuration', link: '/v8/systems/configuration' },
        { text: 'Commands', link: '/v8/systems/commands' }
      ]
    },
    {
      text: 'Utilities',
      items: [
        { text: 'Functions API', link: '/v8/utilities/functions/' },
        { text: 'Client Functions', link: '/v8/utilities/functions/client' },
        { text: 'Item Functions', link: '/v8/utilities/functions/item' },
        { text: 'Math Functions', link: '/v8/utilities/functions/math' },
        { text: 'Player Functions', link: '/v8/utilities/functions/player' },
        { text: 'World Functions', link: '/v8/utilities/functions/world' },
        { text: 'Deprecated', link: '#divider' },
        { text: 'Item Helpers', link: '/v8/utilities/items' },
        { text: 'Inventory Helpers', link: '/v8/utilities/inventory' },
        { text: 'Player Helpers', link: '/v8/utilities/player' },
        { text: 'Level Helpers', link: '/v8/utilities/level' },
        { text: 'Sound Helpers', link: '/v8/utilities/sound' },
        { text: 'Math Utilities', link: '/v8/utilities/math' },
        { text: 'Keybind Helpers', link: '/v8/utilities/keybinds' },
        { text: 'Enchantment Helpers', link: '/v8/utilities/enchantments' }
      ]
    },
    {
      text: 'Advanced',
      items: [
        { text: 'Cross-Platform Development', link: '/v8/advanced/cross-platform' },
        { text: 'Best Practices', link: '/v8/advanced/best-practices' },
        { text: 'Migration Guide', link: '/v8/advanced/migration' }
      ]
    }
  ]
}
