import { useEffect } from "react";

export const usePageTitle = (title: string) => {
  useEffect(() => {
    document.title = "NetWorkUp | " + title;
  }, [title]);
};
