import { Category } from "~/types/category.type";

export const useCategoryStore = defineStore("category-store", () => {
    const selectedCategory = ref<Category>();

    const getCategory = computed(() => selectedCategory.value);

    function setCategory(category: Category | undefined) {
        selectedCategory.value = category;
    }

    return {
        selectedCategory,
        getCategory,
        setCategory
    }
})