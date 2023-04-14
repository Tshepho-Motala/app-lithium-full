// Sorry Anele, but we're not much closer to the way we want things,
// let's wait until we've finalised the models before we do unit tests

// import { shallowMount } from '@vue/test-utils'
// import './vuetify-setup'
// import RewardProviderMock from "@/mock/provider/RewardProviderMock"
// import { Reward } from "../../src/plugin/rewards/Reward"

// describe('Rewards Provider Unit Tests', () => {
  
//     let rewardProvider: RewardProviderMock
//     let listOfRewards: Array<any> = []
//     let mockReward = {}

//     beforeAll(() => {
//         rewardProvider = new RewardProviderMock()

//         listOfRewards = [
//             {
//               id: 1,
//               type: "Cash",
//               name: "Freespins",
//               code: "FREE_CASH",
//               enabled: false,
//               description: "10 freespins",
//               domain: "livescore_media"
//             },
//             {
//               id: 2,
//               type: "Unlock Games",
//               name: "Freebets",
//               code: "UNLOCK_FREE_BETS",
//               enabled: true,
//               description: "15 freebets",
//               domain: "livescore_bet_uk"
//             },
//             {
//               id: 3,
//               type: "Cash",
//               name: "Instant rewards",
//               code: "INSTANT_CASH_REWARS",
//               enabled: false,
//               description: "$10 cash reward",
//               domain: "livescore_bet_uk"
//             },
//             {
//               id: 4,
//               type: "Unlock Games",
//               name: "Instant reward freespins",
//               code: "FREE_REWARD",
//               enabled: true,
//               description: "10 freespins",
//               domain: "livescore_media"
//             },
//           ]

//           mockReward = {
//             type: "Instant Reward",
//             name: "Freespins",
//             code: "FREE_CASH",
//             enabled: false,
//             description: "6 freespins",
//             domain: "livescore_ng"
//       }
//     })

//     //Postive Matches
//     test('Get the list of rewards and Match by the test object to pass', async ()=> {
//         const resultsReward = await rewardProvider.loadRewards()
//         expect(listOfRewards[0]).toMatchObject(resultsReward[0])
//     })

//     test('Save a mock reward and check if it contains an id', async ()=> { 
//         const addedReward  = await rewardProvider.saveReward(mockReward)
//         expect(addedReward.id).toEqual(5)
//     })

//     test('Get reward by id, id and reward domain should match', async ()=> {
//         let rewardId = 1;
//         const searchedReward  = await rewardProvider.getRewardById(rewardId)
//         expect(searchedReward).toEqual(
//             expect.objectContaining({
//                 id: rewardId,
//                 domain: 'livescore_media'
//             })
//         )
//     })

//     test('Update reward with an invalid id, test to throw an error', async ()=> {
//         let rewardUpdate = {
//             type: "Cash",
//             name: "Instant rewards",
//             code: "INSTANT_CASH_REWARS",
//             enabled: false,
//             description: "$10 cash reward"
//         }
//         expect.assertions(1)
//         try {
//             await rewardProvider.updateReward(5, rewardUpdate)
            
//         } catch (error) {
//             expect(error).toEqual('Can not find reward')
//         }
//     })

//     test('Update reward: enable a reward with the specified id', async ()=> {
//         let rewardUpdate = {
//             type: "Cash",
//             name: "Instant rewards",
//             code: "INSTANT_CASH_REWARS",
//             enabled: true,
//             description: "$10 cash reward"
//         }
//        const updatedReward =  await rewardProvider.updateReward(3, rewardUpdate)
//        expect(updatedReward.enabled).not.toBeFalsy()
//     })

//     //Negative matches
//     test('Get reward by id, id and reward domain should not match', async ()=> {
//         let rewardId = 1;
//         const searchedReward  = await rewardProvider.getRewardById(rewardId)
//         expect(searchedReward).toEqual(
//             expect.not.objectContaining({
//                 id: 3,
//                 domain: 'livescore_uk'
//             })
//         )
//     })

// })
