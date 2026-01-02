# InvisibleGear

Hide your armor and held items from other players while still getting the protection.

## About

This plugin lets players hide their equipped armor and held items. You still get all the protection and effects, but other players see you without armor. Useful for PvP servers, roleplay, or anyone who wants a cleaner look.

## Features

- Hide helmet, chestplate, leggings, boots
- Hide main hand and off-hand items
- Hide elytra
- Persists through relogs
- Per-player toggle
- No ProtocolLib required (uses native Paper API)
- Configurable per slot

## Commands

| Command | Description |
|---------|-------------|
| `/vanishgear` | Toggle all gear visibility |
| `/vanishgear <slot>` | Toggle specific slot |
| `/invisiblegear reload` | Reload config |
| `/invisiblegear status` | Show current status |

Slots: helmet, chestplate, leggings, boots, mainhand, offhand

## How It Works

When you hide your gear, other players see you without that equipment. On your screen, everything looks normal. You keep all protection, enchantments, and effects.

The plugin sends fake equipment data to other players. Your actual gear stays exactly the same.

## Permissions

| Permission | Description |
|------------|-------------|
| `invisiblegear.use` | Use /vanishgear command |
| `invisiblegear.admin` | Admin commands |
| `invisiblegear.see` | See hidden gear on others |

## Configuration

- Choose which slots can be hidden
- Set default visibility state
- Customize messages
- Enable/disable persistence

## Support

Discord: <https://discord.gg/7fQPG4Grwt>

## Requirements

- Minecraft 1.21+
- Paper (required for native equipment API)
- Java 21+

## License

MIT License
