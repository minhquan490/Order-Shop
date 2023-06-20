import { Product } from "~/types/product.type";

export const useProductStore = defineStore("product-store", () => {
  const initProduct = (): Product => {
    return {
      id: "",
      name: "",
      size: "",
      price: "",
      categories: [],
      color: "",
      description: "",
      pictures: [],
      taobao_url: "",
      orderPoint: "0",
      isActive: false,
    };
  };

  const selectedProduct = ref<Product>(initProduct());

  const getProduct = computed(() => {
    return selectedProduct.value;
  });

  function setProduct(product: Product) {
    selectedProduct.value = product;
  }

  function $reset() {
    selectedProduct.value = initProduct();
  }

  return {
    selectedProduct,
    getProduct,
    setProduct,
    $reset,
  };
});
