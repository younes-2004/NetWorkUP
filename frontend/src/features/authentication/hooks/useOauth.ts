import { FormEvent, useState } from "react";
import { Button } from "../../../../components/Button/Button";
import { Input } from "../../../../components/Input/Input";
import { useAuth } from "../../hooks/useAuth";  // استخدام hook المعدل

export function Signup() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const { doAuth, errorMessage, isLoading } = useAuth("signup");

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    doAuth(email, password);
  };

  return (
    <form onSubmit={handleSubmit}>
      <Input label="Email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
      <Input label="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
      {errorMessage && <p>{errorMessage}</p>}
      <Button type="submit" disabled={isLoading}>
        {isLoading ? "Signing up..." : "Sign Up"}
      </Button>
    </form>
  );
}
