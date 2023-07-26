    # Cobblemon Outbreaks
A mod that adds outbreaks to Cobblemon!
Thanks to Shadows of Fire for his GatewaysToEternity: https://github.com/Shadows-of-Fire/GatewaysToEternity


## Json
#### Example
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
"shiny_chance": 1024.0,
"experience_reward": 0,
"spawn_range": 25.0,
"leash_range": 32.0,
"spawn_algorithm": "open_field",
"gate_timer": 36000,
"level_algorithm": "scaled"
"min_pokemon_level": 1 [Example] (Does nothing needs min_max algorithm)
"max_pokemon_level": 10 [Example] (Does nothing needs min_max algorithm)
}

"species": [Mandatory] Represents the species of the entity associated with the outbreak portal. It is a string value.

"max_pokemon_level": Indicates the max level of the pokemon that will spawn in the outbreak if none are speficied it will default to 100, needs to be within 2 to 100.

"waves": [Mandatory] Indicates the number of waves in the outbreak. It is an integer value.

"spawns_per_wave":[Mandatory] Specifies the number of entity spawns per wave. It is an integer value within the range of 1 to 64.

"rewards": Represents a list of rewards associated with the outbreak portal. It is an optional field and defaults to an empty list if not specified.

"rewards": Represents a list of biomes for the outbreak portal. It is an optional field and defaults to a plains biome if not specified.

"shiny_chance": Specifies the chance of encountering a shiny entity during the outbreak. It is an optional field with a default value of 1024.0 if not specified. The value should be within the range of 1 to 10,000,000.

"experience_reward": Indicates the amount of experience rewarded for participating in the outbreak. It is an optional field with a default value of 0 if not specified.

"spawn_range": [Mandatory] Represents the range within which entities can spawn around the outbreak portal. It is a double value within the range of 15.0 to 40.0.

"leash_range": Represents the maximum distance that entities can move away from the outbreak portal before being leashed back. It is an optional field with a default value of 32.0 if not specified. The value should be within the range of 15.0 to 40.0.

"spawn_algorithm": Specifies the spawn algorithm used for entity spawns during the outbreak. It is an optional field with a default value of "open_field" if not specified. The value should be one of the predefined spawn algorithms.

"gate_timer": Represents the duration of the outbreak gate timer in ticks. It is an optional field with a default value of 36000 if not specified.
"min_pokemon_level": the minimum level of the pokemon that should spawn in the portal
"max_pokemon_level": the maximum level of the pokemon that should spawn in the portal, should always be higher than min_pokemon_level

"level_algorithm": Specifies the algorithm used to determine the levels for the pokemon, these being: cobblemonoutbreaks:random, cobblemonoutbreaks:scaled, cobblemonoutbreaks:min_max, defaults to cobblemonoutbreaks:scaled

cobblemonoutbreaks:random: returns a random level from 1 to 100
cobblemonoutbreaks:scaled returns a level based on the average pokemon level in the players party
cobblemonoutbreaks:min_max returns a random level between the min_pokemon_level and max_pokemon_level
