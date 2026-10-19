// Hiển thị "Đang tải..." hoặc lỗi. Lỗi cần đăng nhập thì kèm nút đăng nhập.
export default function LoadState({ loading, error, openAuth }) {
  if (loading) return <p className="state">Đang tải...</p>;
  if (!error) return null;
  return (
    <div className="state state--error" role="alert">
      <p>{error}</p>
      {error.includes("đăng nhập") && openAuth && (
        <button className="btn" onClick={() => openAuth("login")}>Đăng nhập</button>
      )}
    </div>
  );
}
