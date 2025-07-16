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
} from '../ui/card'
import Markdown from 'react-markdown'

export default function Analysis({ analysis }: { analysis: AnalysisType[] }) {
  return (
    <div>
      <h2>Analysis:</h2>
      <Accordion type="single" collapsible>
        {analysis.map((analysis) => (
          <AccordionItem value={analysis.id} key={analysis.id}>
            <AccordionTrigger>
              <strong>Repository:</strong> {analysis.repository} <br />
            </AccordionTrigger>
            <AccordionContent>
              {analysis.content.map((content) => (
                <div key={content.filename} className="mb-4">
                  <Card>
                    <CardHeader>
                      <CardTitle>{content.filename}</CardTitle>
                      <CardDescription>{content.summary}</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="mb-2 w-[700px] rounded-md border text-left">
                        <strong>Related Docs:</strong>{' '}
                        <Markdown>
                          {content.related_docs.length > 0
                            ? content.related_docs.join(', ')
                            : 'None'}
                        </Markdown>
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
