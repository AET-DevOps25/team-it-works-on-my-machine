import type { AnalysisType } from '@/lib/types'
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@radix-ui/react-accordion'
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
} from './ui/card'
import Markdown from 'react-markdown'

export default function Analysis({ analysis }: { analysis: AnalysisType[] }) {
  return (
    <div>
      <h2>Analysis:</h2>
      <Accordion
        type="single"
        collapsible
        className="w-full"
        defaultValue="item-1"
      >
        {analysis.map((analysis) => (
          <AccordionItem value={analysis.id} key={analysis.id}>
            <AccordionTrigger
              style={{
                backgroundColor:
                  analysis.id === 'unknown' ? 'red' : 'transparent',
              }}
            >
              <strong>Repository:</strong> {analysis.repository} -{' '}
              {analysis.created_at.toLocaleString('de-DE', {
                timeZone: 'Europe/Berlin',
              })}
            </AccordionTrigger>
            <AccordionContent className="flex flex-col gap-4 text-balance">
              {analysis.content.map((content) => (
                <div key={content.filename} className="mb-4">
                  <Card>
                    <CardHeader>
                      <CardTitle>{content.filename}</CardTitle>
                      <CardDescription>{content.summary}</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="mb-2 w-[700px] rounded-md border text-left">
                        <strong>Related Docs: </strong>
                        <ul>
                          {content.related_docs.length > 0 ? (
                            content.related_docs.map((doc) => {
                              return (
                                <li key={doc}>
                                  <a href={doc}>{doc}</a>
                                </li>
                              )
                            })
                          ) : (
                            <li>None</li>
                          )}
                        </ul>
                      </div>
                      <div>
                        <strong>Detailed Analysis:</strong>
                        <div className="w-[700px] rounded-md border p-4 mt-1 text-muted-foreground text-left">
                          <Markdown>{content.detailed_analysis}</Markdown>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                </div>
              ))}
            </AccordionContent>
          </AccordionItem>
        ))}
      </Accordion>
    </div>
  )
}
