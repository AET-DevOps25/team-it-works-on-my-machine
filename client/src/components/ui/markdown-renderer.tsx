/* eslint-disable @typescript-eslint/no-unsafe-return */
/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
import Markdown from 'react-markdown'

import { cn } from '@/lib/utils'
import type { JSX } from 'react/jsx-runtime'

interface MarkdownRendererProps {
  children: string
}

export function MarkdownRenderer({ children }: MarkdownRendererProps) {
  return (
    <div className="space-y-3">
      <Markdown components={COMPONENTS}>{children}</Markdown>
    </div>
  )
}

const COMPONENTS = {
  h1: withClass('h1', 'text-2xl font-semibold'),
  h2: withClass('h2', 'font-semibold text-xl'),
  h3: withClass('h3', 'font-semibold text-lg'),
  h4: withClass('h4', 'font-semibold text-base'),
  h5: withClass('h5', 'font-medium'),
  strong: withClass('strong', 'font-semibold'),
  a: withClass('a', 'text-primary underline underline-offset-2'),
  blockquote: withClass('blockquote', 'border-l-2 border-primary pl-4'),
  code: ({ children, className, node, ...rest }: any) => {
    return (
      <code
        className={cn(
          'font-mono [:not(pre)>&]:rounded-md [:not(pre)>&]:bg-background/50 [:not(pre)>&]:px-1 [:not(pre)>&]:py-0.5',
        )}
        {...rest}
      >
        {children}
      </code>
    )
  },
  pre: ({ children }: any) => children,
  ol: withClass('ol', 'list-decimal space-y-2 pl-6'),
  ul: withClass('ul', 'list-disc space-y-2 pl-6'),
  li: withClass('li', 'my-1.5'),
  table: withClass(
    'table',
    'w-full border-collapse overflow-y-auto rounded-md border border-foreground/20',
  ),
  th: withClass(
    'th',
    'border border-foreground/20 px-4 py-2 text-left font-bold [&[align=center]]:text-center [&[align=right]]:text-right',
  ),
  td: withClass(
    'td',
    'border border-foreground/20 px-4 py-2 text-left [&[align=center]]:text-center [&[align=right]]:text-right',
  ),
  tr: withClass('tr', 'm-0 border-t p-0 even:bg-muted'),
  p: withClass('p', 'whitespace-pre-wrap'),
  hr: withClass('hr', 'border-foreground/20'),
}

function withClass(Tag: keyof JSX.IntrinsicElements, classes: string) {
  const Component = ({ node, ...props }: any) => (
    <Tag className={classes} {...props} />
  )
  Component.displayName = Tag
  return Component
}

export default MarkdownRenderer
