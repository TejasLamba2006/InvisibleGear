# Changelog

All notable changes to InvisibleGear will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-01-XX

### Added
- Initial release of InvisibleGear
- **Core Features:**
  - Hide armor (helmet, chestplate, leggings, boots)
  - Hide main hand items (weapons, tools)
  - Hide off-hand items (shields, totems)
  - Hide elytra
- **Native Implementation:**
  - Uses Paper's sendEquipmentChange API
  - No ProtocolLib dependency required
  - Efficient packet-based hiding
- **Persistence System:**
  - Save/restore on quit/join
  - Optional keep on death
  - File or memory storage options
- **Visual Effects:**
  - Customizable particles (CLOUD, SMOKE, etc.)
  - Configurable sounds for hide/show
  - Status messages
- **Commands:**
  - `/invisiblegear` (aliases: `/ig`, `/hidearmor`, `/hidegear`, `/stealth`)
  - `/vanishgear` (alias: `/vg`) - Quick toggle
  - Subcommands: toggle, hide, show, status, reload
- **Permissions:**
  - `invisiblegear.use` - Basic usage (default: everyone)
  - `invisiblegear.others` - Modify other players (default: OP)
  - `invisiblegear.bypass` - Always see real gear (default: false)
  - `invisiblegear.admin` - Admin commands (default: OP)
- **Configuration Options:**
  - Choose what to hide (armor, weapons, off-hand, elytra)
  - Cooldown system with bypass permission
  - Per-world enable/disable
  - Max distance optimization
  - Custom messages
- **Event Handling:**
  - Auto-update on equipment change
  - Handle item swap, drop, pickup
  - World change detection
  - Death/respawn handling
- Updated for Minecraft 1.21+
- MIT License
