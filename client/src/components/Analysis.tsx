import type { AnalysisType } from '@/lib/types'
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion'
import MarkdownRenderer from '@/components/ui/markdown-renderer'

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
                      <strong className="text-2xl">Summary:</strong>
                      <div className="mb-2 w-full rounded-md border p-4 text-left">
                        {content.summary}
                      </div>
                      <strong className="text-2xl">Related Docs:</strong>
                      <div className="mb-2 w-full rounded-md border p-4 text-left">
                        <ul>
                          {content.related_docs.length > 0 ? (
                            content.related_docs.map((doc) => {
                              return (
                                <li key={doc} className="list-disc list-inside">
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
                      <strong className="text-2xl">Detailed Analysis:</strong>
                      <div className="w-full rounded-md border p-4 text-left">
                        <MarkdownRenderer>
                          {content.detailed_analysis}
                        </MarkdownRenderer>
                      </div>
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
