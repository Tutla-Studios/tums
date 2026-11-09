package net.tutla.tums.tums;

public enum EventType {
    // client side
    KEY_PRESS,
    LEFT_CLICK,
    LEFT_RELEASE,
    RIGHT_CLICK,
    RIGHT_RELEASE,
    MIDDLE_CLICK,
    MIDDLE_RELEASE,
    SCROLL_UP,
    SCROLL_DOWN,

    ATTACK,

    // server side
    SERVER_START,
    SERVER_ENTITY_LOAD, // TODO: event_entity returns entity's name
}
