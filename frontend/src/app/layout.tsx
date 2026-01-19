import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { Providers } from "@/providers";

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-inter",
});

export const metadata: Metadata = {
  title: "LineageHub - Quản lý gia phả",
  description: "Hệ thống quản lý gia phả dòng họ",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="vi">
      <body className={`${inter.variable} antialiased`}>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
