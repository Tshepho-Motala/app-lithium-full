import { transition, style, trigger, animate, state, group } from "@angular/core";

export let routeAnimation = trigger('routeAnimation', [
  transition('void => *', [
    style({
      opacity: 0,
      transform: 'translate3d(0, 10%, 0)',
    }),
    group([
      animate('4000ms 400ms ease-in-out', style({
        //transform: 'translate3d(0, 0, 0)',
        transform: 'translate3d(0, 0, 0)',
      })),
      animate('4000ms 400ms ease-in-out', style({
        opacity: 1,
      }))
    ]),
  ]),
  transition('* => void', [
    style({
      opacity: 1,
      transform: 'translate3d(0, 0, 0)',
    }),
    group([
      animate('4000ms ease-in-out', style({
        //transform: 'translate3d(0, 0, 0)',
        transform: 'translate3d(0, -10%, 0)',
      })),
      animate('4000ms 150ms ease-in-out', style({
        opacity: 0,
      }))
    ]),
  ]),
]);

export let fadeInAnimation = trigger('fadeInAnimation', [
  transition('void => *', [
    style({
      opacity: 0,
      transform: 'translateX(100%)'
    }),
    animate('400ms 100ms ease-in-out', style({
      transform: 'translateX(0)',
      opacity: 1,
    }))
  ]),
  transition('* => void', [
    animate('400ms ease-in-out', style({
      transform: 'translateX(-100%)',
      opacity: 0,
    }))
  ]),
]);
