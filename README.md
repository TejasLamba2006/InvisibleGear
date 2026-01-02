# InvisibleGear

Hide your armor and weapons from other players for the ultimate stealth experience! Perfect for PvP servers, role-playing, or just surprising your friends.

## ✨ Features

### 🔒 Complete Gear Hiding
- **Hide Armor** - Helmet, chestplate, leggings, boots
- **Hide Weapons** - Main hand items
- **Hide Off-hand** - Shields, totems, maps
- **Hide Elytra** - Complete stealth in the air

### ⚡ Native Implementation
- **No Dependencies** - Works without ProtocolLib!
- Uses Paper's native `sendEquipmentChange` API
- Efficient and lightweight
- Compatible with 1.21+

### 💾 Persistence Options
- Save state on quit/join
- Optional keep on death
- File or memory storage
- Per-world enable/disable

### 🎨 Visual Feedback
- Customizable particles when toggling
- Configurable sounds
- Status messages
- Action bar feedback

### ⚙️ Highly Configurable
- Choose what to hide (armor, weapons, off-hand)
- Cooldown system
- Max distance optimization
- Custom messages

## 📥 Installation

1. Download `InvisibleGear.jar`
2. Place in your `plugins` folder
3. Restart your server
4. Done! No other plugins required!

## 🎯 Quick Start

```
/ig           - Toggle your gear visibility
/ig hide      - Hide your gear
/ig show      - Show your gear
/ig status    - Check your current status
```

## 📋 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/ig` or `/ig toggle` | Toggle your gear visibility | `invisiblegear.use` |
| `/ig hide [player]` | Hide gear | `invisiblegear.use` |
| `/ig show [player]` | Show gear | `invisiblegear.use` |
| `/ig status [player]` | Check gear status | `invisiblegear.use` |
| `/ig reload` | Reload configuration | `invisiblegear.admin` |
| `/vg` | Quick toggle command | `invisiblegear.use` |

**Aliases:** `/ig`, `/hidearmor`, `/hidegear`, `/stealth`

## 🔑 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `invisiblegear.use` | Toggle your own gear visibility | Everyone |
| `invisiblegear.others` | Toggle gear for other players | OP |
| `invisiblegear.always` | Keep gear hidden after death/relog | false |
| `invisiblegear.admin` | Admin commands (reload) | OP |
| `invisiblegear.bypass` | Always see other players' gear | false |
| `invisiblegear.cooldown.bypass` | Bypass toggle cooldown | false |

## ⚙️ Configuration

### What to Hide
```yaml
hide:
  armor: true        # Helmet, chestplate, leggings, boots
  main-hand: true    # Held items
  off-hand: true     # Shields, totems, etc.
  elytra: true       # Wings
```

### Persistence
```yaml
persistence:
  save-on-quit: true      # Remember state
  restore-on-join: true   # Restore on login
  keep-on-death: false    # Keep hidden after death
  storage: FILE           # FILE or MEMORY
```

### Visual Effects
```yaml
visual:
  particles:
    enabled: true
    type: CLOUD
    count: 30
  sounds:
    enabled: true
    hide-sound: ENTITY_ENDERMAN_TELEPORT
    show-sound: ITEM_ARMOR_EQUIP_LEATHER
```

### Performance
```yaml
update:
  refresh-interval: 10   # Ticks between updates
  max-distance: 0        # Max distance (0 = unlimited)
```

### World Settings
```yaml
worlds:
  default: true
  # world_pvp: true
  # creative_world: false
```

## 🎮 Use Cases

### PvP Servers
- Hide your armor to trick enemies
- Appear weaker than you are
- Surprise attacks

### Role-Playing
- Become a "civilian" without visible gear
- Stealth characters
- Disguise gameplay

### Mini-Games
- Hide-and-seek enhancement
- Manhunt stealth mode
- Custom game modes

### Content Creation
- Clean footage without armor clutter
- Dramatic reveals
- Cinematic shots

## 🔌 Technical Details

This plugin uses Paper's native `Player#sendEquipmentChange()` method to show fake empty equipment to other players. This approach:

- ✅ Works without ProtocolLib
- ✅ Is server-side only
- ✅ Doesn't modify actual inventory
- ✅ Updates automatically on equipment change

**Limitations:**
- Players with `invisiblegear.bypass` will always see real gear
- Very briefly visible during chunk loading (fixed by refresh task)

## 🤝 Support

- **Discord:** [Join our Discord](https://discord.gg/7fQPG4Grwt)
- **Issues:** Report bugs on GitHub

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

Made with ❤️ by TejasLamba2006
