import type { Analysis } from '@/lib/types'
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'
import MarkdownRenderer from '@/components/ui/markdown-renderer'
import { Skeleton } from './ui/skeleton'

function Summary({ summary }: { summary: string }) {
  return (
    <>
      <strong className="text-2xl">Summary:</strong>
      <div className="mb-2 w-full rounded-md border p-4 text-left">
        {summary}
      </div>
    </>
  )
}

function RelatedDocs({
  related_docs,
}: {
  related_docs: Analysis['content'][number]['related_docs']
}) {
  return (
    <>
      <strong className="text-2xl">Related Docs:</strong>
      <div className="mb-2 w-full rounded-md border p-4 text-left">
        <ul className="list-disc">
          {related_docs.length > 0 ? (
            related_docs.map((doc) => {
              return (
                <li key={doc} className="hover:underline">
                  <a href={doc} target="_blank">
                    {doc}
                  </a>
                </li>
              )
            })
          ) : (
            <li>None</li>
          )}
        </ul>
      </div>
    </>
  )
}

function DetailedAnalysis({
  detailed_analysis,
}: {
  detailed_analysis: string
}) {
  return (
    <>
      <strong className="text-2xl">Detailed Analysis:</strong>
      <div className="w-full rounded-md border p-4 text-left">
        <MarkdownRenderer>{detailed_analysis}</MarkdownRenderer>
      </div>
    </>
  )
}

function Analysis(analysis: Analysis) {
  return (
    <AccordionItem value={analysis.id}>
      <AccordionTrigger
        className={
          analysis.id === 'unknown'
            ? 'bg-amber-700 p-2 text-center border m-1'
            : 'p-2 text-center border m-1'
        }
      >
        <strong>Repository:</strong> {analysis.repository}
        <span>
          {analysis.created_at.toLocaleString('de-DE', {
            timeZone: 'Europe/Berlin',
          })}
        </span>
      </AccordionTrigger>
      <AccordionContent className="flex flex-col gap-4 text-balance">
        <Accordion type="single" collapsible>
          {analysis.content.map((content) => (
            <AccordionItem value={content.filename} key={content.filename}>
              <AccordionTrigger className="mx-8 my-1 px-2 border">
                {content.filename}
              </AccordionTrigger>
              <AccordionContent className="flex flex-col gap-4 mx-8">
                <Summary summary={content.summary} />
                <DetailedAnalysis
                  detailed_analysis={content.detailed_analysis}
                />
                <RelatedDocs related_docs={content.related_docs} />
              </AccordionContent>
            </AccordionItem>
          ))}
        </Accordion>
      </AccordionContent>
    </AccordionItem>
  )
}

export default function Analyses({
  analyses,
  loading,
}: {
  analyses: Analysis[]
  loading: boolean
}) {
  return (
    <div className="mt-8">
      <h2 className="text-4xl m-2">Analyses:</h2>
      <Accordion type="single" collapsible className="w-full">
        {loading ? (
          <Skeleton className="h-20 w-full" />
        ) : analyses.length === 0 ? (
          <div className="text-center text-gray-500">
            No analyses available yet.
          </div>
        ) : (
          analyses.map((analysis) => (
            <Analysis key={analysis.id} {...analysis} />
          ))
        )}
      </Accordion>
    </div>
  )
}
