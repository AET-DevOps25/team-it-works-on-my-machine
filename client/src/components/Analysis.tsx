import type { AnalysisType } from '@/lib/types'
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'
import MarkdownRenderer from '@/components/ui/markdown-renderer'

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
  related_docs: AnalysisType['content'][number]['related_docs']
}) {
  return (
    <>
      <strong className="text-2xl">Related Docs:</strong>
      <div className="mb-2 w-full rounded-md border p-4 text-left">
        <ul>
          {related_docs.length > 0 ? (
            related_docs.map((doc) => {
              return (
                <li key={doc} className="list-disc list-inside hover:underline">
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

export default function Analysis({ analysis }: { analysis: AnalysisType[] }) {
  return (
    <div className="mt-4">
      <h2 className="text-4xl m-2">Analysis:</h2>
      <Accordion type="single" collapsible className="w-full">
        {analysis.map((analysis) => (
          <AccordionItem value={analysis.id} key={analysis.id}>
            <AccordionTrigger
              className={
                analysis.id === 'unknown'
                  ? 'bg-amber-700 p-2 text-center'
                  : 'p-2 text-center'
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
                  <AccordionItem
                    value={content.filename}
                    key={content.filename}
                  >
                    <AccordionTrigger className="pl-8 pr-8">
                      {content.filename}
                    </AccordionTrigger>
                    <AccordionContent className="flex flex-col gap-4">
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
        ))}
      </Accordion>
    </div>
  )
}
