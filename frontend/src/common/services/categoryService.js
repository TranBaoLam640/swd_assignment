export const getCategories = async () => {
  const response = await fetch(
    "https://dummyjson.com/products/categories"
  );

  return response.json();
};