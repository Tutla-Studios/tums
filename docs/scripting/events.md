---
title: "Events"
summary: "How to react to events in tums"
created: "2026-05-23"
updated: "2026-05-23"
isdoc: true
---

To react to an event use the `on` structure with the desired event to detect.

### Example

```lua
on [event]
    ...
end
```

Example:
```lua
on left_click
    print "you clicked!"
end
```

This will print "you clicked!" every time you left click.

### Event Variables

Some events have variables associated with them. These can be accessed using `event_*`. For example in an `attack` event you can access the `event_entity` to get the entity or if it was a player you can access `event_player`.

Example:
```lua
on attack
    if event_player's health <= 5 then
        print "You are low!"
    else
        print "You are not low!"
    end
end
```

This will print "You are low!" if the player's health is less than or equal to 2.5 hearts

### Event Types

- `left_click` - On every left click. Including menus, holding down the button, etc.
- `left_release` - When left mouse button is released. Including menus and anywhere else within the window.
- `right_click` - On every right click. Including menus, holding down the button, etc.
- `right_release` - When right mouse button is released. Including menus and anywhere else within the window.
- `attack` - When attacked. Includes both players and mobs.
    - `event_player` - You, the [player](/tums/scripting/types/player)
    - `event_entity` - The [entity](/tums/tums/scripting/types/entity) that was attacked