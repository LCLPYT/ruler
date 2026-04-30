# ruler
A Fabric mod for configuring rules for your Minecraft world.
This mod is aiming to be vanilla compatible.

## Features
You can set rules via a command.
Rules impact the behaviour of different game mechanics.
For example, you can disable ice melting, coral death or water freezing.
You can choose to configure each dimension individually.

You can set a rule value with the `/rule set` command (op level 2 or respective permission is required):
```
/rule set <rule> <value>
```

If you want to check the current value of a rule, use `/rule get` (op level 2 or respective permission is required):
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

| Rule                         | Default value | Description                                                |
|------------------------------|---------------|------------------------------------------------------------|
| `ruler:ice_melting`          | `true`        | Whether ice can melt.                                      |
| `ruler:water_freezing`       | `true`        | Whether water can freeze in cold regions.                  |
| `ruler:coral_death`          | `true`        | Whether corals will die outside of water                   |
| `ruler:farmland_trampling`   | `true`        | Whether farmland can be destroyed by jumping on it         |
| `ruler:turtle_egg_trampling` | `true`        | Whether turtle eggs can be destroyed by jumping on them    |
| `ruler:farmland_dry_out`     | `true`        | Whether farmland can dry out when there is no water nearby |
| `ruler:fluid_flow`           | `true`        | Whether fluid flows in the current world                   |

## Permissions

| Feature                 | Required permission                 |
|-------------------------|-------------------------------------|
| Use rule command        | ruler.command.rule                  |
| Use rule set subcommand | ruler.command.rule.set              |
| Modify rule value       | ruler.command.rule.set.&lt;rule&gt; |
| Use rule get subcommand | ruler.command.rule.get              |
| See rule value          | ruler.command.rule.get.&lt;rule&gt; |

## Migration guide
If you want to migrate a world from Minecraft 1.21.11 or earlier to 26.1+, you need to move `<world>/data/ruler.dat` to `<world>/dimensions/<dimension>/data/ruler/rules.dat`.