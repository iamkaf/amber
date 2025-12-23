import type { DefaultTheme } from 'vitepress'

export const v9Sidebar: DefaultTheme.Sidebar = {
  '/v9/': [
    {
      text: 'Introduction',
      items: [
        { text: 'What is Amber?', link: '/v9/' },
        { text: 'Getting Started', link: '/v9/guide/getting-started' },
        { text: 'Installation', link: '/v9/guide/installation' }
      ]
    },
    {
      text: 'Core API',
      items: [
        { text: 'Core Module', link: '/v9/api/core' },
        { text: 'Platform Abstraction', link: '/v9/api/platform' }
      ]
    },
    {
      text: 'Systems',
      items: [
        { text: 'Registry System', link: '/v9/systems/registry' },
        { text: 'Event System', link: '/v9/systems/events' },
        { text: 'Creative Tabs', link: '/v9/systems/creative-tabs' },
        { text: 'Networking', link: '/v9/systems/networking' },
        { text: 'Configuration', link: '/v9/systems/configuration' },
        { text: 'Commands', link: '/v9/systems/commands' }
      ]
    },
    {
      text: 'Utilities',
      items: [
        { text: 'Functions API', link: '/v9/utilities/functions/' },
        { text: 'Client Functions', link: '/v9/utilities/functions/client' },
        { text: 'Item Functions', link: '/v9/utilities/functions/item' },
        { text: 'Math Functions', link: '/v9/utilities/functions/math' },
        { text: 'Player Functions', link: '/v9/utilities/functions/player' },
        { text: 'World Functions', link: '/v9/utilities/functions/world' },
        { text: '─', link: '#divider' },
        { text: 'Item Helpers', link: '/v9/utilities/items' },
        { text: 'Inventory Helpers', link: '/v9/utilities/inventory' },
        { text: 'Player Helpers', link: '/v9/utilities/player' },
        { text: 'Level Helpers', link: '/v9/utilities/level' },
        { text: 'Sound Helpers', link: '/v9/utilities/sound' },
        { text: 'Math Utilities', link: '/v9/utilities/math' },
        { text: 'Keybind Helpers', link: '/v9/utilities/keybinds' },
        { text: 'Enchantment Helpers', link: '/v9/utilities/enchantments' }
      ]
    },
    {
      text: 'Advanced',
      items: [
        { text: 'Cross-Platform Development', link: '/v9/advanced/cross-platform' },
        { text: 'Best Practices', link: '/v9/advanced/best-practices' },
        { text: 'Migration Guide', link: '/v9/advanced/migration' }
      ]
    }
  ]
}
