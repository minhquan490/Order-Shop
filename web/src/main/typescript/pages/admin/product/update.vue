<script lang="ts">

export default {
  setup() {
    const productStore = useProductStore();
    const selectedProduct = productStore.getProduct
    const navigator = useNavigation();
    return {
      productStore,
      selectedProduct,
      navigator
    }
  },
  data() {
    if (process.server) {
      return {};
    }
    return {
      
    }
  },
  mounted() {
    if (this.selectedProduct && this.selectedProduct.id.length === 0) {
      alert('Please select product before access this page');
      this.navigator.navigate('/admin/product');
    }
    this.productStore.$reset();
  }
}
</script>

<template>
  <div>
    Page: foo
  </div>
</template>

<style scoped></style>
