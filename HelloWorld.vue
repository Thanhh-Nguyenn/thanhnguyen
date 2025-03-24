<script setup>
import { ref } from 'vue';

defineProps({
  msg: String,
});

const countIncrement = ref(0);
const countDecrement = ref(0);

const increment = () => {
  countIncrement.value++;
};

const decrement = () => {
  countDecrement.value--;
};
</script>

<template>
  <h1>{{ msg }}</h1>
  <div class="card">
    <button type="button" @click="increment">Cong is {{ countIncrement }} ( + )</button>
    <button type="button" @click="decrement">Tru is {{ countDecrement }} ( - )</button>
  </div>
</template>

<style scoped>
.read-the-docs {
  color: #888;
}
</style>