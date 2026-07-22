export function getPriceUnit(product) {
  return product?.priceUnit === "G" ? "g" : "kg";
}

export function getPackageGrams(product) {
  return Number(product?.priceQuantityGrams ?? (product?.priceUnit === "G" ? 1 : 1000));
}

export function getQuantityStep(product) {
  return 1;
}

export function formatPackage(product) {
  const grams = getPackageGrams(product);
  if (product?.priceUnit === "KG") {
    return `${(grams / 1000).toLocaleString("vi-VN", { maximumFractionDigits: 1 })} kg`;
  }
  return `${grams.toLocaleString("vi-VN")} g`;
}

export function getPackageCount(product, quantity) {
  return Number(quantity ?? 0);
}

export function formatUnitPrice(product) {
  return `${Number(product?.price ?? 0).toLocaleString("vi-VN")} đ/${formatPackage(product)}`;
}

export function formatWeight(grams) {
  const value = Number(grams ?? 0);
  if (value >= 1000 && value % 1000 === 0) {
    return `${(value / 1000).toLocaleString("vi-VN")} kg`;
  }
  return `${value.toLocaleString("vi-VN")} g`;
}

export function calculateLineTotal(product, quantity) {
  const price = Number(product?.price ?? 0);
  return price * Number(quantity ?? 0);
}
