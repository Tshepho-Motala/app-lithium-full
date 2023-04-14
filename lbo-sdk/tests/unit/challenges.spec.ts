// Sorry Anele, but we're not much closer to the way we want things,
// let's wait until we've finalised the models before we do unit tests

// import './vuetify-setup'
// import  ChallengeProvider  from '@/mock/provider/ChallengeProvider'
// import { Challenge } from '../../src/plugin/promotions/challenge/Challenge'
// import GameProvider from '@/plugin/cms/models/GameProvider'

// describe('Challenge Provider unit tests', () => {
//   let challengeProvider: ChallengeProvider
//   let challenge: Challenge
//   let gamesProvider: GameProvider

//   beforeAll(() => {
//     challengeProvider = new ChallengeProvider()
//     gamesProvider = {
//             id: "id",
//             active: true,
//             name: 'Roxor',
//             games: [],
//             domain: 'livescore_uk',
//             addGame(game) {

//             },
//         }

//     challenge = {
//             id: "",
//             completed: "",
//             name: "gettoChance",
//             description: 'xdf',
//             domain: 'livescore_uk',
//             provider: gamesProvider,
//             game: "GoGame",
//             requirement: 'Win Me',
//             reward: 'Instant reward'
//         }

//   })

//   // All To be fixed, the way handlind methods from ChallengeProvider.ts to be refactored
//   test.skip('Get list of challenges: ', async () => {

//     const resultChallenges = await challengeProvider.getChallenges()
//     expect(resultChallenges).toBeUndefined() //Play around this to be Method based on your desired tests

//   })

//   test('Create challenge: ', async () => {

//     const resultChallenges = await challengeProvider.addChallenge(challenge)
//     // Expected Method:
//     expect(resultChallenges).toMatchObject(challenge) //Failing for now, needs to fixed
//   })

//   test('Find challenge by Id: ', async () => {
//     const challengeId = "NRx0mGPVgMDFzs7DoouQC"
//     const resultChallenges = await challengeProvider.getById(challengeId)
//     expect(resultChallenges).toEqual([])

//   })

//   test('Find challenge by provider: ', async () => {
//     const providerChallenge = "Casino"
//     const resultChallenges = await challengeProvider.getByProvider(providerChallenge)
//     // Expected Method:
//     expect(resultChallenges).toEqual([])

//   })

// })
