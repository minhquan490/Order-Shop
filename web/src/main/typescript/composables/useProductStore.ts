import { Product } from "~/types/product.type";

export const useProductStore = defineStore("product-store", () => {
  const product: Product = {
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
  const selectedProduct = ref<Product>(product);

  const getProduct = computed(() => {
    setTimeout(() => {
      $reset();
    }, 300);
    return selectedProduct.value;
  });

  function setProduct(product: Product) {
    selectedProduct.value = product;
  }

  function $reset() {
    selectedProduct.value = {
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
  }

  return {
    selectedProduct,
    getProduct,
    setProduct,
    $reset
  };
});
