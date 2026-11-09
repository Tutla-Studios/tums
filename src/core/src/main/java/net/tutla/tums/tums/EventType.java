package net.tutla.tums.tums;

/// !((warning This Page is still under development!)[Most documentation is incomplete or outdated])
///
/// To react to an event use the `on` structure with the desired event to detect.
///
/// ### Example
///
/// ```lua
/// on [event]
///     ...
/// end
/// ```
///
/// Example:
/// ```lua
/// on left_click
///     print "you clicked!"
/// end
/// ```
///
/// This will print "you clicked!" every time you left click.
///
/// ### Event Variables
///
/// Some events have variables associated with them. These can be accessed using `event_*`. For example in an `attack` event you can access the `event_entity` to get the entity or if it was a player you can access `event_player`.
///
/// Example:
/// ```lua
/// on attack
///     if event_player's health <= 5 then
///         print "You are low!"
///     else
///         print "You are not low!"
///     end
/// end
/// ```
///
/// This will print "You are low!" if the player's health is less than or equal to 2.5 hearts
///
/// ### Event Types
///
/// <!-- events -->
///
/// @tums.title Events
/// @tums.summary How to react to events in tums
public enum EventType {
    // client side
    /// When a key is pressed. *Not fired by any loader yet.*
    KEY_PRESS,
    /// On every left click. Including menus, holding down the button, etc. *Fabric only for now.*
    LEFT_CLICK,
    /// When left mouse button is released. Including menus and anywhere else within the window. *Fabric only for now.*
    LEFT_RELEASE,
    /// On every right click. Including menus, holding down the button, etc. *Not fired by any loader yet.*
    RIGHT_CLICK,
    /// When right mouse button is released. Including menus and anywhere else within the window. *Not fired by any loader yet.*
    RIGHT_RELEASE,
    /// On every middle click. *Not fired by any loader yet.*
    MIDDLE_CLICK,
    /// When the middle mouse button is released. *Not fired by any loader yet.*
    MIDDLE_RELEASE,
    /// When scrolling up. *Not fired by any loader yet.*
    SCROLL_UP,
    /// When scrolling down. *Not fired by any loader yet.*
    SCROLL_DOWN,

    /// When attacked. Includes both players and mobs. *Fabric only for now.*
    /// - `event_player` - You, the [player](https://wiki.tutla.net/tums/scripting/types/player)
    /// - `event_entity` - The [entity](https://wiki.tutla.net/tums/scripting/types/entity) that was attacked
    ATTACK,

    // server side
    /// When the server has started, right after every script has been loaded. *Spigot/Paper only for now.*
    SERVER_START,
    /// When an entity is loaded on the server. *Not fired by any loader yet.*
    SERVER_ENTITY_LOAD, // TODO: event_entity returns entity's name
}
