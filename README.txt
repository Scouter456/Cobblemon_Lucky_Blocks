A mod that adds outbreaks to Cobblemon!
Thanks to Shadows of Fire for his GatewaysToEternity: https://github.com/Shadows-of-Fire/GatewaysToEternity


Example
{
  "species": "gastly",
  "max_pokemon_level": 100,
  "waves": 3,
  "spawns_per_wave": 5,
  "rewards": [
    "cobblemon:rare_candy",
    "minecraft:diamond"
  ],
    "biome": [
    "minecraft:plains"
    ],
  "min_spawn_radius": 48,
  "max_spawn_radius": 128,
  "shiny_chance": 1024.0,
  "experience_reward": 0,
  "spawn_range": 25.0,
  "spawn_algorithm": "open_field",
  "gate_timer": 36000

}


"species": [Mandatory] Represents the species of the entity associated with the outbreak portal. It is a string value.
"max_pokemon_level": Indicates the max level of the pokemon that will spawn in the outbreak if none are speficied it will default to 100, needs to be within 2 to 100.
"waves": [Mandatory] Indicates the number of waves in the outbreak. It is an integer value.
"spawns_per_wave":[Mandatory] Specifies the number of entity spawns per wave. It is an integer value within the range of 1 to 64.
"rewards": Represents a list of rewards associated with the outbreak portal. It is an optional field and defaults to an empty list if not specified.
"rewards": Represents a list of biomes for the outbreak portal. It is an optional field and defaults to a plains biome if not specified.

"min_spawn_radius": Represents the minimum radius for entity spawns around the outbreak portal. It is an optional field with a default value of 48 if not specified. The value should be within the range of 20 to 72.
"max_spawn_radius": Represents the maximum radius for entity spawns around the outbreak portal. It is an optional field with a default value of 128 if not specified. The value should be within the range of 72 to 256.
"shiny_chance": Specifies the chance of encountering a shiny entity during the outbreak. It is an optional field with a default value of 1024.0 if not specified. The value should be within the range of 1 to 10,000,000.
"experience_reward": Indicates the amount of experience rewarded for participating in the outbreak. It is an optional field with a default value of 0 if not specified.
"spawn_range": [Mandatory] Represents the range within which entities can spawn around the outbreak portal. It is a double value within the range of 15.0 to 40.0.
"leash_range": Represents the maximum distance that entities can move away from the outbreak portal before being leashed back. It is an optional field with a default value of 32.0 if not specified. The value should be within the range of 15.0 to 40.0.
"spawn_algorithm": Specifies the spawn algorithm used for entity spawns during the outbreak. It is an optional field with a default value of "open_field" if not specified. The value should be one of the predefined spawn algorithms.
"gate_timer": Represents the duration of the outbreak gate timer in ticks. It is an optional field with a default value of 36000 if not specified.
