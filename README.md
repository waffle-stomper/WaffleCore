# WaffleCore

## Shared components for various waffle_stomper mods

Notes: 

 - This repository contains code derived from work by TealNerd: https://gist.github.com/TealNerd/db5bae695552eaede90008e3f36cbea0
 
 - The mod uses the 'world_id' plugin channel to fetch world UUIDs. It is compatible with either VoxelMap or JourneyMap but not both at the same time, since JourneyMap exclusively uses world_info and VoxelMap will use world_id if world_info is already taken by JourneyMap.