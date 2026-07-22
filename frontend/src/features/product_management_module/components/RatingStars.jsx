export default function RatingStars({ value = 0, count, size = "normal" }) {
  const rounded = Math.round(Number(value || 0));
  const fontSize = size === "sm" ? "0.9rem" : "1rem";

  return (
    <span className="d-inline-flex align-items-center gap-1" style={{ fontSize }}>
      <span aria-label={`${Number(value || 0).toFixed(1)} sao`}>
        {[1, 2, 3, 4, 5].map((star) => (
          <span key={star} className={star <= rounded ? "text-warning" : "text-muted"}>
            ★
          </span>
        ))}
      </span>
      {typeof count === "number" && <span className="text-muted">({count})</span>}
    </span>
  );
}
