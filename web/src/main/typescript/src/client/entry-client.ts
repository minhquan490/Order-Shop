import { createApp } from './main';

const { router } = createApp()

router
  .isReady()
  .then(() => {
    
  })
  .catch((e) => console.log(e));
