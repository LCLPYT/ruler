# ruler
A Fabric mod for configuring rules for your Minecraft world.
This mod is aiming to be vanilla compatible.

## Features
You can set rules via a command.
Rules impact the behaviour of different game mechanics.
For example, you can disable ice melting, coral death or water freezing.
You can choose to configure each dimension individually.

You can set a rule value with the `/rule set` command (op level 2 is required):
```
/rule set <rule> <value>
```

If you want to check the current value of a rule, use `/rule get` (op level 2 too):
```
/rule get <rule>
```

These commands will get or set the rules of the player's current dimension.
When executed by a server, e.g. through the console, `minecraft:overworld` is used by default.

If you want to modify or query the rules of a different dimension, you can pass it to the rule command:
```
/rule set <rule> <value> [dimension]
/rule get <rule> [dimension]
```

## List of rules

| Rule                   | Default value | Description                                       |
|------------------------|---------------|---------------------------------------------------|
| `ruler:ice_melting`    | `true`        | Controls whether ice can melt or not.             |
| `ruler:water_freezing` | `true`        | Controls whether water can freeze.                |
| `ruler:coral_death`    | `true`        | Controls whether corals will die outside of water |