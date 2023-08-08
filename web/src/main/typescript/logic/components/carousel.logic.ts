import carousel from '~/components/Carousel.vue';

type CarouselComponent = InstanceType<typeof carousel>

const goPrevious = (component: CarouselComponent): void => {
    if ((!component.items) || component.items.length === 0) {
        return;
    }
    const temp = component.currentIndex;
    if (component.currentIndex === 0) {
        component.currentIndex = component.items.length - 1;
    } else {
        component.currentIndex -= 1;
    }
    component.items[temp].current = false;
    component.items[component.currentIndex].current = true;
}

const goNext = (component: CarouselComponent): void => {
    if ((!component.items) || component.items.length === 0) {
        return;
    }
    const temp = component.currentIndex;
    if (component.currentIndex === component.items.length - 1) {
        component.currentIndex = 0;
    } else {
        component.currentIndex += 1;
    }
    component.items[temp].current = false;
    component.items[component.currentIndex].current = true;
}

export {
    goNext,
    goPrevious
}